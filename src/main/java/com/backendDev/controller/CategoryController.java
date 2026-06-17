package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CategoryRequest;
import com.backendDev.dto.CategoryResponse;
import com.backendDev.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesByAdmin(
            @PathVariable String adminId) {
        return ResponseEntity.ok(categoryService.getCategoriesByAdmin(adminId));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long categoryId,
            @RequestParam String adminId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId, adminId));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @RequestParam String adminId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, adminId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long categoryId,
            @RequestParam String adminId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId, adminId));
    }
}