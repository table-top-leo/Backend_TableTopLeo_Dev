package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.ProductRequest;
import com.backendDev.dto.ProductResponse;
import com.backendDev.model.Category;
import com.backendDev.model.Product;
import com.backendDev.repo.CategoryRepository;
import com.backendDev.repo.ProductRepository;
import com.backendDev.service.FileStorageService;
import com.backendDev.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        Category category = categoryRepository.findByCategoryIdAndAdminId(
                        request.getCategoryId(), request.getAdminId())
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        Product product = Product.builder()
                .adminId(request.getAdminId())
                .businessId(request.getBusinessId())
                .categoryId(request.getCategoryId())
                .itemName(request.getItemName().trim())
                .itemDescription(nullIfBlank(request.getItemDescription()))
                .itemPrice(request.getItemPrice())
                .itemImageUrl(nullIfBlank(request.getItemImageUrl()))
                .productStatus(request.getProductStatus() != null ? request.getProductStatus() : "ACTIVE")
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: {} in category: {} for admin: {}",
                saved.getProductId(), request.getCategoryId(), request.getAdminId());

        return ApiResponse.success("Product created successfully.", mapToResponse(saved, category.getCategoryName()));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ProductResponse>> getProductsByAdmin(String adminId) {
        List<Product> products = productRepository.findByAdminIdOrderByCreatedAtDesc(adminId);

        List<ProductResponse> responses = products.stream()
                .map(p -> {
                    String catName = categoryRepository.findById(p.getCategoryId())
                            .map(Category::getCategoryName)
                            .orElse("Unknown");
                    return mapToResponse(p, catName);
                })
                .collect(Collectors.toList());

        return ApiResponse.success("Products fetched successfully.", responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ProductResponse>> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        List<Product> products = productRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);

        List<ProductResponse> responses = products.stream()
                .map(p -> mapToResponse(p, category.getCategoryName()))
                .collect(Collectors.toList());

        return ApiResponse.success("Products fetched successfully.", responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ProductResponse> getProductById(Long productId, String adminId) {
        Product product = productRepository.findByProductIdAndAdminId(productId, adminId)
                .orElseThrow(() -> new AppException("Product not found.", HttpStatus.NOT_FOUND));

        String catName = categoryRepository.findById(product.getCategoryId())
                .map(Category::getCategoryName)
                .orElse("Unknown");

        return ApiResponse.success("Product fetched successfully.", mapToResponse(product, catName));
    }

    @Override
    @Transactional
    public ApiResponse<ProductResponse> updateProduct(Long productId, String adminId, ProductRequest request) {
        Product product = productRepository.findByProductIdAndAdminId(productId, adminId)
                .orElseThrow(() -> new AppException("Product not found.", HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findByCategoryIdAndAdminId(
                        request.getCategoryId(), adminId)
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        String oldImageUrl = product.getItemImageUrl();
        String newImageUrl = nullIfBlank(request.getItemImageUrl());

        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            String oldFilename = fileStorageService.extractFilename(oldImageUrl);
            fileStorageService.deleteFile("products", oldFilename);
        }

        product.setCategoryId(request.getCategoryId());
        product.setItemName(request.getItemName().trim());
        product.setItemDescription(nullIfBlank(request.getItemDescription()));
        product.setItemPrice(request.getItemPrice());
        product.setItemImageUrl(newImageUrl);
        if (request.getProductStatus() != null) {
            product.setProductStatus(request.getProductStatus());
        }

        Product updated = productRepository.save(product);
        log.info("Product updated: {} for admin: {}", productId, adminId);

        return ApiResponse.success("Product updated successfully.", mapToResponse(updated, category.getCategoryName()));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteProduct(Long productId, String adminId) {
        Product product = productRepository.findByProductIdAndAdminId(productId, adminId)
                .orElseThrow(() -> new AppException("Product not found.", HttpStatus.NOT_FOUND));

        String imgFilename = fileStorageService.extractFilename(product.getItemImageUrl());
        fileStorageService.deleteFile("products", imgFilename);

        productRepository.delete(product);
        log.info("Product deleted: {} for admin: {}", productId, adminId);

        return ApiResponse.success("Product deleted successfully.");
    }

    private ProductResponse mapToResponse(Product product, String categoryName) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .adminId(product.getAdminId())
                .businessId(product.getBusinessId())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .itemName(product.getItemName())
                .itemDescription(product.getItemDescription())
                .itemPrice(product.getItemPrice())
                .itemImageUrl(product.getItemImageUrl())
                .productStatus(product.getProductStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}