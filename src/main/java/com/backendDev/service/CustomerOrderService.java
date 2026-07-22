package com.backendDev.service;

import com.backendDev.dto.*;

public interface CustomerOrderService {

    ApiResponse<CreateSessionResponse>    createSession(CreateSessionRequest request);

    ApiResponse<PlaceOrderResponse>       placeOrder(PlaceOrderRequest request);

    ApiResponse<InitiatePaymentResponse>  initiatePayment(InitiatePaymentRequest request);

    ApiResponse<ConfirmPaymentResponse>   confirmPayment(ConfirmPaymentRequest request);

    ApiResponse<OrderStatusResponse>      getOrderStatus(String orderId);

    // ── Invoice (Task 1) ───────────────────────────────────────────
    // GET: assembles invoice details from existing tables (order, items, business, session)
    ApiResponse<InvoiceDetailsResponse>   getInvoiceDetails(String orderId);

    // POST: stores the given email on the session, then sends the invoice
    // ONLY if the email is present and payment has completed successfully.
    ApiResponse<SendInvoiceEmailResponse> sendInvoiceEmail(String orderId, SendInvoiceEmailRequest request);
}
