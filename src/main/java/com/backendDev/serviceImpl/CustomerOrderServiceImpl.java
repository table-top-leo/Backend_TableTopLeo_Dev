package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.*;
import com.backendDev.model.*;
import com.backendDev.repo.*;
import com.backendDev.service.CustomerOrderService;
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

    private final CustomerSessionRepository   sessionRepo;
    private final CustomerOrderRepository     orderRepo;
    private final OrderItemRepository         itemRepo;
    private final OrderPaymentRepository      paymentRepo;
    private final BusinessInformationRepository businessRepo;
    private final PaymentConfigurationRepository paymentConfigRepo;
    private final ObjectMapper objectMapper;

    private static final BigDecimal TAX_RATE       = new BigDecimal("0.05");
    private static final int        EXPIRY_HOURS    = 2;
    private static final int        EST_MINUTES     = 20;
    @Override
    @Transactional
    public ApiResponse<CreateSessionResponse> createSession(CreateSessionRequest request) {
        BusinessInformation business = businessRepo.findByBusinessId(request.getBusinessId())
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        CustomerSession session = CustomerSession.builder()
                .sessionId("SES-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
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
    @Override
    @Transactional
    public ApiResponse<PlaceOrderResponse> placeOrder(PlaceOrderRequest request) {
        // 1. Validate session — accept ACTIVE or ORDERED (customer may switch payment method)
        CustomerSession session = sessionRepo.findBySessionIdAndBusinessId(
                        request.getSessionId(), request.getBusinessId())
                .orElseThrow(() -> new AppException("Session not found or expired.", HttpStatus.BAD_REQUEST));

        if (!"ACTIVE".equals(session.getSessionStatus()) && !"ORDERED".equals(session.getSessionStatus())) {
            throw new AppException("Session is no longer active. Please scan the QR code again.", HttpStatus.BAD_REQUEST);
        }
        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            throw new AppException("Session has expired. Please scan QR again.", HttpStatus.BAD_REQUEST);
        }

        // 2. Validate Take Away requires name & phone
        if ("TAKE_AWAY".equals(request.getOrderType())) {
            if (request.getCustomerName() == null || request.getCustomerName().isBlank())
                throw new AppException("Name is required for Take Away.", HttpStatus.BAD_REQUEST);
            if (request.getCustomerPhone() == null || request.getCustomerPhone().isBlank())
                throw new AppException("Phone number is required for Take Away.", HttpStatus.BAD_REQUEST);
        }

        // 3. Calculate totals
        BigDecimal subtotal = request.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax      = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total    = subtotal.add(tax);

        // 4. Build order
        String orderId     = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String orderNumber = "TL-" + System.currentTimeMillis() % 100000;

        CustomerOrder order = CustomerOrder.builder()
                .orderId(orderId)
                .orderNumber(orderNumber)
                .sessionId(session.getSessionId())
                .adminId(session.getAdminId())
                .businessId(session.getBusinessId())
                .orderType(request.getOrderType())
                .tableNumber(request.getTableNumber())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .customerNote(request.getCustomerNote())
                .subtotal(subtotal)
                .taxAmount(tax)
                .discountAmount(BigDecimal.ZERO)
                .grandTotal(total)
                .paymentStatus("PENDING")
                .orderStatus("PLACED")
                .estimatedMinutes(EST_MINUTES)
                .build();

        orderRepo.save(order);

        // 5. Save order items as snapshots
        List<OrderItem> savedItems = request.getItems().stream().map(i -> {
            OrderItem item = OrderItem.builder()
                    .itemId("ITM-" + UUID.randomUUID().toString().replace("-","").substring(0,10).toUpperCase())
                    .orderId(orderId)
                    .adminId(session.getAdminId())
                    .businessId(session.getBusinessId())
                    .productId(i.getProductId())
                    .productName(i.getProductName())
                    .productDescription(i.getProductDescription())
                    .productImageUrl(i.getProductImageUrl())
                    .categoryName(i.getCategoryName())
                    .unitPrice(i.getUnitPrice())
                    .quantity(i.getQuantity())
                    .lineTotal(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .specialRequest(i.getSpecialRequest())
                    .build();
            return itemRepo.save(item);
        }).collect(Collectors.toList());

        // 6. Update session
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
                .grandTotal(total).paymentStatus("PENDING").orderStatus("PLACED")
                .items(itemDtos).createdAt(order.getCreatedAt())
                .build());
    }

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
        String gatewayDb = request.getGatewayName().toUpperCase(); // DB stores UPI, RAZORPAY, STRIPE, PAYPAL
        String currency  = request.getCurrency() != null ? request.getCurrency() : "INR";
        BigDecimal amount = order.getGrandTotal();

        PaymentConfiguration config = paymentConfigRepo
                .findByBusinessIdAndPaymentType(order.getBusinessId(), gatewayDb)
                .orElseThrow(() -> new AppException(
                        "Payment method " + gateway + " is not configured for this business.", HttpStatus.BAD_REQUEST));

        InitiatePaymentResponse.InitiatePaymentResponseBuilder responseBuilder =
                InitiatePaymentResponse.builder()
                        .paymentId(paymentId).orderId(order.getOrderId())
                        .gatewayName(gateway).amount(amount)
                        .currency(currency).paymentStatus("PENDING");

        OrderPayment payment = OrderPayment.builder()
                .paymentId(paymentId).orderId(order.getOrderId())
                .adminId(order.getAdminId()).businessId(order.getBusinessId())
                .gatewayName(gateway).paidAmount(amount).currency(currency)
                .paymentStatus("PENDING").build();

        // ── UPI: build the deep-link string ──────────────────────
        if ("upi".equals(gateway)) {
            String upiId   = config.getUpiId();
            String merchant = config.getMerchantName() != null ? config.getMerchantName() : order.getBusinessId();
            String upiStr  = String.format("upi://pay?pa=%s&pn=%s&am=%.2f&cu=INR&tn=Order%%23%s",
                    upiId, merchant.replace(" ", "+"), amount, order.getOrderNumber());
            responseBuilder.upiString(upiStr);
            payment.setPaymentReference(upiId);
        }

        // ── RAZORPAY ─────────────────────────────────────────────
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
                log.info("Razorpay order created: {} for orderId: {}", rzpOrderId, order.getOrderId());
            } catch (Exception e) {
                log.error("Razorpay order creation failed: {}", e.getMessage());
                throw new AppException("Failed to initiate Razorpay payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // ── STRIPE ───────────────────────────────────────────────
        if ("stripe".equals(gateway)) {
            try {
                Stripe.apiKey = config.getSecretKey();
                long amountInSmallestUnit = "INR".equals(currency)
                        ? amount.multiply(BigDecimal.valueOf(100)).longValue()
                        : amount.multiply(BigDecimal.valueOf(100)).longValue();
                PaymentIntent intent = PaymentIntent.create(
                        PaymentIntentCreateParams.builder()
                                .setAmount(amountInSmallestUnit)
                                .setCurrency(currency.toLowerCase())
                                .putMetadata("order_id", order.getOrderId())
                                .putMetadata("order_number", order.getOrderNumber())
                                .setAutomaticPaymentMethods(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                                .setEnabled(true).build())
                                .build());
                responseBuilder.stripeClientSecret(intent.getClientSecret())
                        .stripePublishableKey(config.getPublishableKey());
                payment.setTransactionId(intent.getId());
                log.info("Stripe PaymentIntent created: {} for orderId: {}", intent.getId(), order.getOrderId());
            } catch (Exception e) {
                log.error("Stripe initiation failed: {}", e.getMessage());
                throw new AppException("Failed to initiate Stripe payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // ── PAYPAL ───────────────────────────────────────────────
        if ("paypal".equals(gateway)) {
            // PayPal order is created on frontend using JS SDK + client_id
            // Backend only provides the client_id here
            responseBuilder.paypalClientId(config.getPaypalClientId())
                    .paypalOrderId("PAYPAL-PENDING-" + System.currentTimeMillis());
        }

        paymentRepo.save(payment);
        order.setPaymentMethod(gateway);
        orderRepo.save(order);

        return ApiResponse.success("Payment initiated", responseBuilder.build());
    }
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

        // ── UPI: mark as paid directly (payment happened via UPI app) ──
        if ("upi".equals(gateway)) {
            payment.setTransactionId(request.getTransactionId());
            payment.setPaymentReference(request.getPaymentReference());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
        }

        // ── RAZORPAY: verify signature ────────────────────────────
        if ("razorpay".equals(gateway)) {
            try {
                PaymentConfiguration config = paymentConfigRepo
                        .findByBusinessIdAndPaymentType(order.getBusinessId(), "RAZORPAY")
                        .orElseThrow(() -> new AppException("Razorpay config not found.", HttpStatus.BAD_REQUEST));

                String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
                String generatedSignature = hmacSha256(payload, config.getSecretKey());

                if (!generatedSignature.equals(request.getRazorpaySignature())) {
                    payment.setPaymentStatus("FAILED");
                    payment.setFailureReason("Signature verification failed.");
                    paymentRepo.save(payment);
                    throw new AppException("Payment verification failed. Invalid signature.", HttpStatus.BAD_REQUEST);
                }

                payment.setTransactionId(request.getRazorpayPaymentId());
                payment.setPaymentReference(request.getRazorpayOrderId());
                payment.setPaymentStatus("SUCCESS");
                payment.setCompletedAt(LocalDateTime.now());
                log.info("Razorpay payment verified: {}", request.getRazorpayPaymentId());
            } catch (AppException ae) {
                throw ae;
            } catch (Exception e) {
                throw new AppException("Razorpay verification error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // ── STRIPE: client confirms via frontend, we trust client secret match ──
        if ("stripe".equals(gateway)) {
            payment.setTransactionId(request.getStripePaymentIntentId());
            payment.setPaymentReference(request.getStripePaymentIntentId());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
            log.info("Stripe payment confirmed: {}", request.getStripePaymentIntentId());
        }

        // ── PAYPAL: capture ID from frontend ─────────────────────
        if ("paypal".equals(gateway)) {
            payment.setTransactionId(request.getPaypalCaptureId());
            payment.setPaymentReference(request.getPaypalOrderId());
            payment.setPaymentStatus("SUCCESS");
            payment.setCompletedAt(LocalDateTime.now());
            log.info("PayPal payment confirmed: captureId={}", request.getPaypalCaptureId());
        }

        // Save gateway raw response for audit
        payment.setGatewayResponse(request.getGatewayResponse());
        paymentRepo.save(payment);

        // Update order status
        order.setPaymentStatus("PAID");
        order.setOrderStatus("ACCEPTED");
        orderRepo.save(order);

        log.info("Order {} confirmed and PAID via {}", order.getOrderId(), gateway);

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

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}