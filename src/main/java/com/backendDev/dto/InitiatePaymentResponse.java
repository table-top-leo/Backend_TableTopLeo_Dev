package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InitiatePaymentResponse {
    private String paymentId;
    private String orderId;
    private String gatewayName;
    private BigDecimal amount;
    private String currency;
    private String upiString;          // for UPI — full upi:// deep link
    private String razorpayOrderId;    // for Razorpay — create order on backend
    private String razorpayKeyId;      // Razorpay publishable key
    private String stripeClientSecret; // for Stripe PaymentIntent
    private String stripePublishableKey;
    private String paypalOrderId;      // for PayPal
    private String paypalClientId;
    private String paymentStatus;
}
