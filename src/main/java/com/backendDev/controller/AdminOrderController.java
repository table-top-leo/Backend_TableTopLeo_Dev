package com.backendDev.controller;

import com.backendDev.dto.AdminOrderResponse;
import com.backendDev.dto.ApiResponse;
import com.backendDev.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminOrderController.class);

    public static final String GET_ALL_ORDERS   = "";
    public static final String GET_ORDER_DETAIL = "/{orderId}";
    public static final String UPDATE_STATUS    = "/{orderId}/status";

    private final AdminOrderService adminOrderService;

    @GetMapping(value = GET_ALL_ORDERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getAllOrders() {
        LOG.info("Admin fetching all orders");
        ApiResponse<List<AdminOrderResponse>> response = adminOrderService.getAllOrdersForAdmin();
        LOG.info("Returning {} orders", response.getData() != null ? response.getData().size() : 0);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = GET_ORDER_DETAIL, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AdminOrderResponse>> getOrderDetail(
            @PathVariable String orderId) {
        LOG.info("Admin fetching order detail for orderId: {}", orderId);
        return ResponseEntity.ok(adminOrderService.getOrderDetailById(orderId));
    }

    @PutMapping(value = UPDATE_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AdminOrderResponse>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        LOG.info("Admin updating order status for orderId: {}, status: {}", orderId, status);
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(orderId, status));
    }
}
