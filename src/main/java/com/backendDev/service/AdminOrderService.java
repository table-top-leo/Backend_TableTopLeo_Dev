package com.backendDev.service;

import com.backendDev.dto.AdminOrderResponse;
import com.backendDev.dto.ApiResponse;
import java.util.List;

public interface AdminOrderService {

    ApiResponse<List<AdminOrderResponse>> getAllOrdersForAdmin();

    ApiResponse<AdminOrderResponse> getOrderDetailById(String orderId);

    ApiResponse<AdminOrderResponse> updateOrderStatus(String orderId, String newStatus);
}
