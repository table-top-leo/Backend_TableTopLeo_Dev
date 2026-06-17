package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CategoryRequest;
import com.backendDev.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    ApiResponse<CategoryResponse> createCategory(CategoryRequest request);

    ApiResponse<List<CategoryResponse>> getCategoriesByAdmin(String adminId);

    ApiResponse<CategoryResponse> getCategoryById(Long categoryId, String adminId);

    ApiResponse<CategoryResponse> updateCategory(Long categoryId, String adminId, CategoryRequest request);

    ApiResponse<Void> deleteCategory(Long categoryId, String adminId);
}