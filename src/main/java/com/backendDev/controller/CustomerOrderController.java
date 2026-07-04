package com.backendDev.controller;

import com.backendDev.dto.*;
import com.backendDev.service.CustomerOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderController.class);

    public static final String CREATE_SESSION     = "/session";
    public static final String PLACE_ORDER        = "/order";
    public static final String INITIATE_PAYMENT   = "/payment/initiate";
    public static final String CONFIRM_PAYMENT    = "/payment/confirm";
    public static final String GET_ORDER_STATUS   = "/order/{orderId}/status";

    private final CustomerOrderService customerOrderService;

    @PostMapping(value = CREATE_SESSION, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CreateSessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest request) {
        LOG.info("Creating customer session for businessId: {}", request.getBusinessId());
        ApiResponse<CreateSessionResponse> response = customerOrderService.createSession(request);
        LOG.info("Session created: {}", response.getData() != null ? response.getData().getSessionId() : "null");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = PLACE_ORDER, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PlaceOrderResponse>> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {
        LOG.info("Placing order for sessionId: {}, businessId: {}, orderType: {}", request.getSessionId(), request.getBusinessId(), request.getOrderType());
        ApiResponse<PlaceOrderResponse> response = customerOrderService.placeOrder(request);
        LOG.info("Order placed: {}", response.getData() != null ? response.getData().getOrderId() : "null");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = INITIATE_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<InitiatePaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        LOG.info("Initiating payment for orderId: {}, gateway: {}", request.getOrderId(), request.getGatewayName());
        ApiResponse<InitiatePaymentResponse> response = customerOrderService.initiatePayment(request);
        LOG.info("Payment initiated: {}", response.getData() != null ? response.getData().getPaymentId() : "null");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = CONFIRM_PAYMENT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ConfirmPaymentResponse>> confirmPayment(
            @Valid @RequestBody ConfirmPaymentRequest request) {
        LOG.info("Confirming payment for orderId: {}, paymentId: {}, gateway: {}", request.getOrderId(), request.getPaymentId(), request.getGatewayName());
        ApiResponse<ConfirmPaymentResponse> response = customerOrderService.confirmPayment(request);
        LOG.info("Payment confirmed with status: {}", response.getData() != null ? response.getData().getPaymentStatus() : "null");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = GET_ORDER_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<OrderStatusResponse>> getOrderStatus(
            @PathVariable String orderId) {
        LOG.info("Fetching order status for orderId: {}", orderId);
        ApiResponse<OrderStatusResponse> response = customerOrderService.getOrderStatus(orderId);
        LOG.info("Order status: {}", response.getData() != null ? response.getData().getOrderStatus() : "null");
        return ResponseEntity.ok(response);
    }
}