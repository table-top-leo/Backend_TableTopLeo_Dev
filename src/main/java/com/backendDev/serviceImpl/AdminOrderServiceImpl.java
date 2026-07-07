package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.AdminOrderResponse;
import com.backendDev.dto.ApiResponse;
import com.backendDev.model.CustomerOrder;
import com.backendDev.model.OrderItem;
import com.backendDev.repo.CustomerOrderRepository;
import com.backendDev.repo.OrderItemRepository;
import com.backendDev.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {

    private final CustomerOrderRepository orderRepo;
    private final OrderItemRepository     itemRepo;

    @Override
    public ApiResponse<List<AdminOrderResponse>> getAllOrdersForAdmin() {
        String adminId = UserContext.getAdminId();
        log.info("Fetching all orders for adminId: {}", adminId);

        List<CustomerOrder> orders = orderRepo.findAllByAdminIdOrderByCreatedAtDesc(adminId);
        log.info("Found {} orders for adminId: {}", orders.size(), adminId);

        List<AdminOrderResponse> responses = orders.stream()
                .map(order -> toResponse(order, itemRepo.findAllByOrderId(order.getOrderId())))
                .collect(Collectors.toList());

        return ApiResponse.success("Orders fetched successfully", responses);
    }

    @Override
    public ApiResponse<AdminOrderResponse> getOrderDetailById(String orderId) {
        String adminId = UserContext.getAdminId();
        log.info("Fetching order detail for orderId: {}, adminId: {}", orderId, adminId);

        CustomerOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        if (!order.getAdminId().equals(adminId)) {
            throw new AppException("Access denied. This order does not belong to your account.", HttpStatus.FORBIDDEN);
        }

        List<OrderItem> items = itemRepo.findAllByOrderId(orderId);
        return ApiResponse.success("Order detail fetched successfully", toResponse(order, items));
    }

    @Override
    @Transactional
    public ApiResponse<AdminOrderResponse> updateOrderStatus(String orderId, String newStatus) {
        String adminId = UserContext.getAdminId();
        log.info("Updating order status for orderId: {}, newStatus: {}, adminId: {}", orderId, newStatus, adminId);

        CustomerOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new AppException("Order not found.", HttpStatus.NOT_FOUND));

        if (!order.getAdminId().equals(adminId)) {
            throw new AppException("Access denied.", HttpStatus.FORBIDDEN);
        }

        List<String> validStatuses = List.of("PLACED", "ACCEPTED", "PREPARING", "READY", "COMPLETED", "CANCELLED");
        if (!validStatuses.contains(newStatus.toUpperCase())) {
            throw new AppException("Invalid status: " + newStatus, HttpStatus.BAD_REQUEST);
        }

        order.setOrderStatus(newStatus.toUpperCase());
        orderRepo.save(order);
        log.info("Order {} status updated to {}", orderId, newStatus);

        List<OrderItem> items = itemRepo.findAllByOrderId(orderId);
        return ApiResponse.success("Order status updated successfully", toResponse(order, items));
    }

    private AdminOrderResponse toResponse(CustomerOrder order, List<OrderItem> items) {
        List<AdminOrderResponse.AdminOrderItemResponse> itemResponses = items.stream()
                .map(i -> AdminOrderResponse.AdminOrderItemResponse.builder()
                        .itemId(i.getItemId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .productDescription(i.getProductDescription())
                        .productImageUrl(i.getProductImageUrl())
                        .categoryName(i.getCategoryName())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .lineTotal(i.getLineTotal())
                        .specialRequest(i.getSpecialRequest())
                        .build())
                .collect(Collectors.toList());

        return AdminOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .adminId(order.getAdminId())
                .businessId(order.getBusinessId())
                .sessionId(order.getSessionId())
                .orderType(order.getOrderType())
                .tableNumber(order.getTableNumber())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .customerNote(order.getCustomerNote())
                .subtotal(order.getSubtotal())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .grandTotal(order.getGrandTotal())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .payAtCounter(order.getPayAtCounter())
                .estimatedMinutes(order.getEstimatedMinutes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }
}
