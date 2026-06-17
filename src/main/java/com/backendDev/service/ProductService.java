package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.ProductRequest;
import com.backendDev.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ApiResponse<ProductResponse> createProduct(ProductRequest request);

    ApiResponse<List<ProductResponse>> getProductsByAdmin(String adminId);

    ApiResponse<List<ProductResponse>> getProductsByCategory(Long categoryId);

    ApiResponse<ProductResponse> getProductById(Long productId, String adminId);

    ApiResponse<ProductResponse> updateProduct(Long productId, String adminId, ProductRequest request);

    ApiResponse<Void> deleteProduct(Long productId, String adminId);
}