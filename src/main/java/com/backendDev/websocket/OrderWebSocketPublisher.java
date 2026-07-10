package com.backendDev.websocket;

import com.backendDev.dto.WebSocketEvent;
import com.backendDev.model.CustomerOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    private static final Map<String, String> STATUS_MESSAGES = Map.of(
        "PLACED",    "Your order has been received.",
        "ACCEPTED",  "Your order has been accepted by the restaurant.",
        "PREPARING", "👨‍🍳 Our chef is now preparing your order.",
        "READY",     "🍽️ Your order is ready for pickup!",
        "COMPLETED", "🎉 Thank you! Enjoy your meal.",
        "CANCELLED", "❌ Your order has been cancelled."
    );

    // ── Called when a NEW ORDER is placed ───────────────────────
    // Publishes to admin's topic — admin dashboard receives it instantly
    public void publishNewOrder(CustomerOrder order) {
        String adminTopic = "/topic/admin/" + order.getAdminId() + "/orders";

        WebSocketEvent event = WebSocketEvent.builder()
                .eventType("NEW_ORDER")
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .adminId(order.getAdminId())
                .businessId(order.getBusinessId())
                .sessionId(order.getSessionId())
                .orderType(order.getOrderType())
                .tableNumber(order.getTableNumber())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .grandTotal(order.getGrandTotal())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .payAtCounter(order.getPayAtCounter())
                .orderStatus(order.getOrderStatus())
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend(adminTopic, event);
        log.info("WebSocket NEW_ORDER published to {} for orderId: {}", adminTopic, order.getOrderId());
    }

    // ── Called when ADMIN UPDATES STATUS ────────────────────────
    // Publishes to TWO topics:
    //   1. Admin topic — so other admin dashboards update
    //   2. Order-specific topic — so customer tracking updates
    public void publishStatusUpdate(CustomerOrder order, String previousStatus) {
        String adminTopic  = "/topic/admin/" + order.getAdminId() + "/orders";
        String orderTopic  = "/topic/order/" + order.getOrderId() + "/status";

        WebSocketEvent event = WebSocketEvent.builder()
                .eventType("STATUS_UPDATED")
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .adminId(order.getAdminId())
                .businessId(order.getBusinessId())
                .orderStatus(order.getOrderStatus())
                .previousStatus(previousStatus)
                .statusMessage(STATUS_MESSAGES.getOrDefault(order.getOrderStatus(), "Order status updated."))
                .estimatedMinutes(order.getEstimatedMinutes())
                .grandTotal(order.getGrandTotal())
                .paymentStatus(order.getPaymentStatus())
                .timestamp(LocalDateTime.now())
                .build();

        // Send to admin dashboard (order list updates)
        messagingTemplate.convertAndSend(adminTopic, event);

        // Send to customer tracking page (their order status updates)
        messagingTemplate.convertAndSend(orderTopic, event);

        log.info("WebSocket STATUS_UPDATED published. orderId: {} status: {} -> {}",
                order.getOrderId(), previousStatus, order.getOrderStatus());
    }
}
