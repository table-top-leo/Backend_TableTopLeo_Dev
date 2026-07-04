package com.backendDev.service;

import com.backendDev.dto.*;

public interface CustomerOrderService {

    ApiResponse<CreateSessionResponse>    createSession(CreateSessionRequest request);

    ApiResponse<PlaceOrderResponse>       placeOrder(PlaceOrderRequest request);

    ApiResponse<InitiatePaymentResponse>  initiatePayment(InitiatePaymentRequest request);

    ApiResponse<ConfirmPaymentResponse>   confirmPayment(ConfirmPaymentRequest request);

    ApiResponse<OrderStatusResponse>      getOrderStatus(String orderId);
}
