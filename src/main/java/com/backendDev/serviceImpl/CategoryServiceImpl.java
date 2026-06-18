package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CategoryRequest;
import com.backendDev.dto.CategoryResponse;
import com.backendDev.model.Category;
import com.backendDev.repo.CategoryRepository;
import com.backendDev.repo.ProductRepository;
import com.backendDev.service.CategoryService;
import com.backendDev.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCategoryNameIgnoreCaseAndAdminId(
                request.getCategoryName(), request.getAdminId())) {
            throw new AppException(
                    "Category '" + request.getCategoryName() + "' already exists.",
                    HttpStatus.CONFLICT
            );
        }

        Category category = Category.builder()
                .adminId(request.getAdminId())
                .businessId(request.getBusinessId())
                .categoryName(request.getCategoryName().trim())
                .categoryImageUrl(nullIfBlank(request.getCategoryImageUrl()))
                .categoryStatus(request.getCategoryStatus() != null ? request.getCategoryStatus() : "ACTIVE")
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created: {} for admin: {}", saved.getCategoryId(), request.getAdminId());

        return ApiResponse.success("Category created successfully.", mapToResponse(saved, 0L));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryResponse>> getCategoriesByAdmin(String adminId) {
        List<Category> categories = categoryRepository.findByAdminIdOrderByCreatedAtDesc(adminId);

        Map<Long, Long> countMap = productRepository
                .countProductsGroupedByCategoryForAdmin(adminId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        List<CategoryResponse> responses = categories.stream()
                .map(cat -> mapToResponse(cat, countMap.getOrDefault(cat.getCategoryId(), 0L)))
                .collect(Collectors.toList());

        return ApiResponse.success("Categories fetched successfully.", responses);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CategoryResponse> getCategoryById(Long categoryId, String adminId) {
        Category category = categoryRepository.findByCategoryIdAndAdminId(categoryId, adminId)
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        long count = productRepository.countByCategoryId(categoryId);
        return ApiResponse.success("Category fetched successfully.", mapToResponse(category, count));
    }

    @Override
    @Transactional
    public ApiResponse<CategoryResponse> updateCategory(Long categoryId, String adminId, CategoryRequest request) {
        Category category = categoryRepository.findByCategoryIdAndAdminId(categoryId, adminId)
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        boolean nameExists = categoryRepository
                .existsByCategoryNameIgnoreCaseAndAdminId(request.getCategoryName(), adminId);
        if (nameExists && !category.getCategoryName().equalsIgnoreCase(request.getCategoryName())) {
            throw new AppException(
                    "Category '" + request.getCategoryName() + "' already exists.",
                    HttpStatus.CONFLICT
            );
        }

        String oldImageUrl = category.getCategoryImageUrl();
        String newImageUrl = nullIfBlank(request.getCategoryImageUrl());

        if (oldImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            String oldFilename = fileStorageService.extractFilename(oldImageUrl);
            fileStorageService.deleteFile("categories", oldFilename);
        }

        category.setCategoryName(request.getCategoryName().trim());
        category.setCategoryImageUrl(newImageUrl);
        if (request.getCategoryStatus() != null) {
            category.setCategoryStatus(request.getCategoryStatus());
        }

        Category updated = categoryRepository.save(category);
        long count = productRepository.countByCategoryId(categoryId);
        log.info("Category updated: {} for admin: {}", categoryId, adminId);

        return ApiResponse.success("Category updated successfully.", mapToResponse(updated, count));
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteCategory(Long categoryId, String adminId) {
        Category category = categoryRepository.findByCategoryIdAndAdminId(categoryId, adminId)
                .orElseThrow(() -> new AppException("Category not found.", HttpStatus.NOT_FOUND));

        List<com.backendDev.model.Product> products =
                productRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
        for (com.backendDev.model.Product p : products) {
            String imgFilename = fileStorageService.extractFilename(p.getItemImageUrl());
            fileStorageService.deleteFile("products", imgFilename);
        }

        String catFilename = fileStorageService.extractFilename(category.getCategoryImageUrl());
        fileStorageService.deleteFile("categories", catFilename);

        productRepository.deleteAllByCategoryId(categoryId);
        categoryRepository.delete(category);
        log.info("Category deleted: {} for admin: {}", categoryId, adminId);

        return ApiResponse.success("Category and all its products deleted successfully.");
    }

    private CategoryResponse mapToResponse(Category category, long productCount) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .adminId(category.getAdminId())
                .businessId(category.getBusinessId())
                .categoryName(category.getCategoryName())
                .categoryImageUrl(category.getCategoryImageUrl())
                .categoryStatus(category.getCategoryStatus())
                .productCount(productCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}