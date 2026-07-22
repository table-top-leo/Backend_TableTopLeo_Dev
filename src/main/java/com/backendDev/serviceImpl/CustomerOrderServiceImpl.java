package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.*;
import com.backendDev.model.*;
import com.backendDev.repo.*;
import com.backendDev.service.CustomerOrderService;
import com.backendDev.service.EmailService;
import com.backendDev.websocket.OrderWebSocketPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerSessionRepository     sessionRepo;
    private final CustomerOrderRepository       orderRepo;
    private final OrderItemRepository           itemRepo;
    private final OrderPaymentRepository        paymentRepo;
    private final BusinessInformationRepository businessRepo;
    private final PaymentConfigurationRepository paymentConfigRepo;
    private final ObjectMapper                  objectMapper;
    private final OrderWebSocketPublisher       webSocketPublisher;
    private final EmailService                  emailService;

    private static final BigDecimal TAX_RATE     = new BigDecimal("0.05");
    private static final int        EXPIRY_HOURS = 2;
    private static final int        EST_MINUTES  = 20;

    // ── CREATE SESSION ───────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<CreateSessionResponse> createSession(CreateSessionRequest request) {
        BusinessInformation business = businessRepo.findByBusinessId(request.getBusinessId())
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        CustomerSession session = CustomerSession.builder()
                .sessionId("SES-" + UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase())
                .adminId(business.getAdminId())
                .businessId(business.getBusinessId())
                .tableNumber(request.getTableNumber())
                .sessionStatus("ACTIVE")
                .expiresAt(LocalDateTime.now().plusHours(EXPIRY_HOURS))
                .build();

        sessionRepo.save(session);
        log.info("Session created: {} for business: {}", session.getSessionId(), business.getBusinessId());

        return ApiResponse.success("Session created", CreateSessionResponse.builder()
                .sessionId(session.getSessionId())
                .businessId(session.getBusinessId())
                .adminId(session.getAdminId())
                .sessionStatus(session.getSessionStatus())
                .build());
    }

    // ── PLACE ORDER ──────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<PlaceOrderResponse> placeOrder(PlaceOrderRequest request) {
        CustomerSession session = sessionRepo.findBySessionIdAndBusinessId(
                        request.getSessionId(), request.getBusinessId())
                .orElseThrow(() -> new AppException("Session not found or expired.", HttpStatus.BAD_REQUEST));

        if (!"ACTIVE".equals(session.getSessionStatus()) && !"ORDERED".equals(session.getSessionStatus())) {
            throw new AppException("Session is no longer active. Please scan the QR code again.", HttpStatus.BAD_REQUEST);
        }
        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            throw new AppException("Session has expired. Please scan QR again.", HttpStatus.BAD_REQUEST);
        }

        if ("TAKE_AWAY".equals(request.getOrderType())) {
            if (request.getCustomerName() == null || request.getCustomerName().isBlank())
                throw new AppException("Name is required for Take Away.", HttpStatus.BAD_REQUEST);
            if (request.getCustomerPhone() == null || request.getCustomerPhone().isBlank())
                throw new AppException("Phone number is required for Take Away.", HttpStatus.BAD_REQUEST);
        }

        BigDecimal subtotal = request.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax   = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        String orderId     = "ORD-" + UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();
        String orderNumber = "TL-" + System.currentTimeMillis() % 100000;

        CustomerOrder order = CustomerOrder.builder()
                .orderId(orderId).orderNumber(orderNumber)
                .sessionId(session.getSessionId())
                .adminId(session.getAdminId()).businessId(session.getBusinessId())
                .orderType(request.getOrderType())
                .tableNumber(request.getTableNumber())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerNote(request.getCustomerNote())
                .subtotal(subtotal).taxAmount(tax).discountAmount(BigDecimal.ZERO).grandTotal(total)
                .paymentStatus(Boolean.TRUE.equals(request.getPayAtCounter()) ? "PAY_AT_COUNTER" : "PENDING")
                .orderStatus("PLACED")
                .payAtCounter(Boolean.TRUE.equals(request.getPayAtCounter()))
                .estimatedMinutes(EST_MINUTES)
                .build();

        orderRepo.save(order);

        List<OrderItem> savedItems = request.getItems().stream().map(i -> {
            OrderItem item = OrderItem.builder()
                    .itemId("ITM-" + UUID.randomUUID().toString().replace("-","").substring(0,10).toUpperCase())
                    .orderId(orderId).adminId(session.getAdminId()).businessId(session.getBusinessId())
                    .productId(i.getProductId()).productName(i.getProductName())
                    .productDescription(i.getProductDescription()).productImageUrl(i.getProductImageUrl())
                    .categoryName(i.getCategoryName()).unitPrice(i.getUnitPrice())
                    .quantity(i.getQuantity())
                    .lineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .specialRequest(i.getSpecialRequest())
                    .build();
            return itemRepo.save(item);
        }).collect(Collectors.toList());

        session.setOrderType(request.getOrderType());
        session.setCustomerName(request.getCustomerName());
        session.setCustomerPhone(request.getCustomerPhone());
        session.setCustomerEmail(request.getCustomerEmail());
        session.setTableNumber(request.getTableNumber());
        session.setSessionStatus("ORDERED");
        sessionRepo.save(session);

        log.info("Order placed: {} for business: {}", orderId, session.getBusinessId());

        List<PlaceOrderResponse.OrderItemDto> itemDtos = savedItems.stream().map(i ->
                PlaceOrderResponse.OrderItemDto.builder()
                        .itemId(i.getItemId()).productId(i.getProductId())
                        .productName(i.getProductName()).productImageUrl(i.getProductImageUrl())
                        .unitPrice(i.getUnitPrice()).quantity(i.getQuantity())
                        .lineTotal(i.getLineTotal()).specialRequest(i.getSpecialRequest())
                        .build()).collect(Collectors.toList());

        return ApiResponse.success("Order placed successfully", PlaceOrderResponse.builder()
                .orderId(orderId).orderNumber(orderNumber).sessionId(session.getSessionId())
                .adminId(session.getAdminId()).businessId(session.getBusinessId())
                .orderType(request.getOrderType()).tableNumber(request.getTableNumber())
                .customerName(request.getCustomerName()).customerPhone(request.getCustomerPhone())
                .customerNote(request.getCustomerNote()).subtotal(subtotal).taxAmount(tax)
                .grandTotal(total)
                .paymentStatus(Boolean.TRUE.equals(request.getPayAtCounter()) ? "PAY_AT_COUNTER" : "PENDING")
                .orderStatus("PLACED")
                .payAtCounter(Boolean.TRUE.equals(request.getPayAtCounter()))
                .items(itemDtos).createdAt(order.getCreatedAt())
                .build());
    }

    // ── INITIATE PAYMENT ─────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<InitiatePaymentResponse> initiatePayment(InitiatePaymentRequest request) {
        CustomerOrder order = orderRepo.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        if ("PAID".equals(order.getPaymentStatus())) {
            throw new AppException("Order is already paid.", HttpStatus.BAD_REQUEST);
        }

        String paymentId = "PAY-" + UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();
        String gateway   = request.getGatewayName().toLowerCase();
        String gatewayDb = request.getGatewayName().toUpperCase();
        String currency  = request.getCurrency() != null ? request.getCurrency() : "INR";
        BigDecimal amount = order.getGrandTotal();

        // ── PAY AT COUNTER: skip config lookup — no gateway needed ──
        if ("pay_at_counter".equals(gateway)) {
            OrderPayment pacPayment = OrderPayment.builder()
                    .paymentId(paymentId).orderId(order.getOrderId())
                    .adminId(order.getAdminId()).businessId(order.getBusinessId())
                    .gatewayName(gateway).paidAmount(amount).currency(currency)
                    .paymentStatus("PAY_AT_COUNTER")
                    .build();
            paymentRepo.save(pacPayment);
            order.setPaymentMethod(gateway);
            orderRepo.save(order);
            log.info("Pay at Counter payment record created: {}", paymentId);
            return ApiResponse.success("Pay at Counter initiated", InitiatePaymentResponse.builder()
                    .paymentId(paymentId).orderId(order.getOrderId())
                    .gatewayName(gateway).amount(amount).currency(currency)
                    .paymentStatus("PAY_AT_COUNTER")
                    .build());
        }

        PaymentConfiguration config = paymentConfigRepo
                .findByBusinessIdAndPaymentType(order.getBusinessId(), gatewayDb)
                .orElseThrow(() -> new AppException(
                        "Payment method " + gateway + " is not configured for this business.", HttpStatus.BAD_REQUEST));

        InitiatePaymentResponse.InitiatePaymentResponseBuilder responseBuilder =
                InitiatePaymentResponse.builder()
                        .paymentId(paymentId).orderId(order.getOrderId())
                        .gatewayName(gateway).amount(amount).currency(currency).paymentStatus("PENDING");

        OrderPayment payment = OrderPayment.builder()
                .paymentId(paymentId).orderId(order.getOrderId())
                .adminId(order.getAdminId()).businessId(order.getBusinessId())
                .gatewayName(gateway).paidAmount(amount).currency(currency).paymentStatus("PENDING")
                .build();

        if ("upi".equals(gateway)) {
            String upiId   = config.getUpiId();
            String merchant = config.getMerchantName() != null ? config.getMerchantName() : order.getBusinessId();
            String upiStr  = String.format("upi://pay?pa=%s&pn=%s&am=%.2f&cu=INR&tn=Order%%23%s",
                    upiId, merchant.replace(" ","+"), amount, order.getOrderNumber());
            responseBuilder.upiString(upiStr);
            payment.setPaymentReference(upiId);
        }

        if ("razorpay".equals(gateway)) {
            try {
                RazorpayClient razorpay = new RazorpayClient(config.getPublishableKey(), config.getSecretKey());
                JSONObject options = new JSONObject();
                options.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
                options.put("currency", currency);
                options.put("receipt", order.getOrderNumber());
                options.put("notes", new JSONObject().put("order_id", order.getOrderId()));
                Order rzpOrder = razorpay.orders.create(options);
                String rzpOrderId = rzpOrder.get("id");
                responseBuilder.razorpayOrderId(rzpOrderId).razorpayKeyId(config.getPublishableKey());
                payment.setPaymentReference(rzpOrderId);
            } catch (Exception e) {
                throw new AppException("Failed to initiate Razorpay payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if ("stripe".equals(gateway)) {
            try {
                Stripe.apiKey = config.getSecretKey();
                PaymentIntent intent = PaymentIntent.create(
                        PaymentIntentCreateParams.builder()
                                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                .setCurrency(currency.toLowerCase())
                                .putMetadata("order_id", order.getOrderId())
                                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                                .build());
                responseBuilder.stripeClientSecret(intent.getClientSecret()).stripePublishableKey(config.getPublishableKey());
                payment.setTransactionId(intent.getId());
            } catch (Exception e) {
                throw new AppException("Failed to initiate Stripe payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if ("paypal".equals(gateway)) {
            responseBuilder.paypalClientId(config.getPaypalClientId())
                    .paypalOrderId("PAYPAL-PENDING-" + System.currentTimeMillis());
        }

        paymentRepo.save(payment);
        order.setPaymentMethod(gateway);
        orderRepo.save(order);

        return ApiResponse.success("Payment initiated", responseBuilder.build());
    }

    // ── CONFIRM PAYMENT ──────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<ConfirmPaymentResponse> confirmPayment(ConfirmPaymentRequest request) {
        OrderPayment payment = paymentRepo.findByPaymentId(request.getPaymentId())
                .orElseThrow(() -> new AppException("Payment record not found.", HttpStatus.NOT_FOUND));

        CustomerOrder order = orderRepo.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        BusinessInformation business = businessRepo.findByBusinessId(order.getBusinessId())
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        String gateway = request.getGatewayName().toLowerCase();

        if ("upi".equals(gateway)) {
            payment.setTransactionId(request.getTransactionId());
            payment.setPaymentReference(request.getPaymentReference());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
        }

        if ("razorpay".equals(gateway)) {
            try {
                PaymentConfiguration config = paymentConfigRepo
                        .findByBusinessIdAndPaymentType(order.getBusinessId(), "RAZORPAY")
                        .orElseThrow(() -> new AppException("Razorpay config not found.", HttpStatus.BAD_REQUEST));
                String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
                String sig = hmacSha256(payload, config.getSecretKey());
                if (!sig.equals(request.getRazorpaySignature())) {
                    payment.setPaymentStatus("FAILED");
                    payment.setFailureReason("Signature verification failed.");
                    paymentRepo.save(payment);
                    throw new AppException("Payment verification failed. Invalid signature.", HttpStatus.BAD_REQUEST);
                }
                payment.setTransactionId(request.getRazorpayPaymentId());
                payment.setPaymentReference(request.getRazorpayOrderId());
                payment.setPaymentStatus("SUCCESS");
                payment.setCompletedAt(LocalDateTime.now());
            } catch (AppException ae) { throw ae; }
            catch (Exception e) { throw new AppException("Razorpay verification error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
        }

        if ("stripe".equals(gateway)) {
            payment.setTransactionId(request.getStripePaymentIntentId());
            payment.setPaymentReference(request.getStripePaymentIntentId());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
        }

        if ("paypal".equals(gateway)) {
            payment.setTransactionId(request.getPaypalCaptureId());
            payment.setPaymentReference(request.getPaypalOrderId());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
        }

        if ("pay_at_counter".equals(gateway)) {
            payment.setPaymentStatus("PAY_AT_COUNTER");
            payment.setCompletedAt(LocalDateTime.now());
        }

        payment.setGatewayResponse(request.getGatewayResponse());
        paymentRepo.save(payment);

        // Pay at counter stays as PAY_AT_COUNTER — customer pays later at cashier
        // All other gateways = already paid online → PAID
        if ("pay_at_counter".equals(gateway)) {
            order.setPaymentStatus("PAY_AT_COUNTER");
        } else {
            order.setPaymentStatus("PAID");
        }
        order.setOrderStatus("ACCEPTED");
        orderRepo.save(order);

        // ── PUBLISH WebSocket NEW_ORDER event to admin dashboard ──
        webSocketPublisher.publishNewOrder(order);
        log.info("Order {} confirmed PAID — WebSocket event published to admin", order.getOrderId());

        return ApiResponse.success("Payment confirmed. Order accepted!", ConfirmPaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .grandTotal(order.getGrandTotal())
                .gatewayName(gateway)
                .businessName(business.getBusinessName())
                .orderType(order.getOrderType())
                .customerName(order.getCustomerName())
                .estimatedMinutes(order.getEstimatedMinutes())
                .createdAt(order.getCreatedAt())
                .build());
    }

    // ── GET ORDER STATUS ─────────────────────────────────────────
    @Override
    public ApiResponse<OrderStatusResponse> getOrderStatus(String orderId) {
        CustomerOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        return ApiResponse.success("Order status fetched", OrderStatusResponse.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .estimatedMinutes(order.getEstimatedMinutes())
                .grandTotal(order.getGrandTotal())
                .orderType(order.getOrderType())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build());
    }

    // ── GET INVOICE DETAILS (Task 1) ───────────────────────────────
    // Pulls everything from tables that already exist — no invoice table.
    @Override
    public ApiResponse<InvoiceDetailsResponse> getInvoiceDetails(String orderId) {
        InvoiceDetailsResponse invoice = assembleInvoice(orderId);
        return ApiResponse.success("Invoice details fetched", invoice);
    }

    // ── SEND INVOICE EMAIL (Task 1) ─────────────────────────────────
    // Stores ONLY the email on the customer session, then sends the invoice
    // ONLY if the email is present and the order's payment has completed (PAID).
    @Override
    @Transactional
    public ApiResponse<SendInvoiceEmailResponse> sendInvoiceEmail(String orderId, SendInvoiceEmailRequest request) {
        CustomerOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        CustomerSession session = sessionRepo.findBySessionId(order.getSessionId())
                .orElseThrow(() -> new AppException("Session not found for this order.", HttpStatus.NOT_FOUND));

        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        if (email == null || email.isBlank()) {
            throw new AppException("Email is required to send the invoice.", HttpStatus.BAD_REQUEST);
        }

        // Store ONLY the email on tabletop_leo_customer_sessions.customer_email
        session.setCustomerEmail(email);
        sessionRepo.save(session);
        log.info("Customer email stored on session {} for order {}", session.getSessionId(), orderId);

        boolean paymentCompleted = "PAID".equalsIgnoreCase(order.getPaymentStatus());
        if (!paymentCompleted) {
            log.info("Invoice email skipped for order {} — payment not completed yet (status: {})",
                    orderId, order.getPaymentStatus());
            return ApiResponse.success("Email saved. Invoice will be sent once payment is completed.",
                    SendInvoiceEmailResponse.builder()
                            .orderId(orderId).email(email).emailSent(false)
                            .message("Payment not completed yet.")
                            .build());
        }

        // Reuse the exact same assembly logic as the GET API
        InvoiceDetailsResponse invoice = assembleInvoice(orderId);
        invoice.setCustomerEmail(email);

        emailService.sendInvoiceEmail(email, invoice);
        log.info("Invoice email dispatched for order {} to {}", orderId, email);

        return ApiResponse.success("Invoice sent successfully", SendInvoiceEmailResponse.builder()
                .orderId(orderId).email(email).emailSent(true)
                .message("Invoice emailed successfully.")
                .build());
    }

    // ── Shared invoice assembly (used by GET API and by sendInvoiceEmail) ──
    private InvoiceDetailsResponse assembleInvoice(String orderId) {
        CustomerOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        BusinessInformation business = businessRepo.findByBusinessId(order.getBusinessId())
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        String customerEmail = sessionRepo.findBySessionId(order.getSessionId())
                .map(CustomerSession::getCustomerEmail)
                .orElse(null);

        List<OrderItem> orderItems = itemRepo.findAllByOrderId(orderId);
        List<InvoiceDetailsResponse.InvoiceItemDto> items = orderItems.stream()
                .map(i -> InvoiceDetailsResponse.InvoiceItemDto.builder()
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .lineTotal(i.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        String paymentMethod = paymentRepo.findByOrderId(orderId)
                .map(OrderPayment::getGatewayName)
                .orElse(order.getPaymentMethod());

        return InvoiceDetailsResponse.builder()
                .invoiceNumber(order.getOrderNumber())
                .orderId(order.getOrderId())
                .adminId(order.getAdminId())
                .businessId(order.getBusinessId())
                .businessName(business.getBusinessName())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(customerEmail)
                .tableNumber(order.getTableNumber())
                .orderType(order.getOrderType())
                .subtotal(order.getSubtotal())
                .gstAmount(order.getTaxAmount())
                .grandTotal(order.getGrandTotal())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(paymentMethod)
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    // ── HELPER ───────────────────────────────────────────────────
    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}