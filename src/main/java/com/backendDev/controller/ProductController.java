package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.ProductRequest;
import com.backendDev.dto.ProductResponse;
import com.backendDev.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByAdmin(
            @PathVariable String adminId) {
        return ResponseEntity.ok(productService.getProductsByAdmin(adminId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long productId,
            @RequestParam String adminId) {
        return ResponseEntity.ok(productService.getProductById(productId, adminId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @RequestParam String adminId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, adminId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId,
            @RequestParam String adminId) {
        return ResponseEntity.ok(productService.deleteProduct(productId, adminId));
    }
}