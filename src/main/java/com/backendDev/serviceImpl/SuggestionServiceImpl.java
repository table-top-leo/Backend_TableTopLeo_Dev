package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.*;
import com.backendDev.model.BusinessInformation;
import com.backendDev.model.CategorySuggestion;
import com.backendDev.model.ItemSuggestion;
import com.backendDev.repo.BusinessInformationRepository;
import com.backendDev.repo.CategorySuggestionRepository;
import com.backendDev.repo.ItemSuggestionRepository;
import com.backendDev.service.SuggestionService;
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
public class SuggestionServiceImpl implements SuggestionService {

    private final CategorySuggestionRepository catSuggRepo;
    private final ItemSuggestionRepository itemSuggRepo;
    private final BusinessInformationRepository businessRepo;

    // ════════════════════════════════════════════════════════
    // GET — Auto-resolve from JWT (used by menu page)
    // ════════════════════════════════════════════════════════

    @Override
    public ApiResponse<List<CategorySuggestionResponse>> getCategorySuggestions() {
        String adminId = UserContext.getAdminId();
        String businessType = resolveBusinessType(adminId);
        log.info("Loading category suggestions for adminId={}, businessType={}", adminId, businessType);

        List<CategorySuggestionResponse> list = catSuggRepo
                .findByBusinessTypeOrderByDisplayOrderAsc(businessType)
                .stream()
                .map(this::toCatResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Category suggestions loaded", list);
    }

    @Override
    public ApiResponse<List<ItemSuggestionResponse>> getItemSuggestions(String categoryName) {
        String adminId = UserContext.getAdminId();
        String businessType = resolveBusinessType(adminId);
        log.info("Loading item suggestions for adminId={}, businessType={}, category={}", adminId, businessType, categoryName);

        List<ItemSuggestionResponse> list = itemSuggRepo
                .findByBusinessTypeAndCategoryNameOrderByDisplayOrderAsc(businessType, categoryName)
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Item suggestions loaded", list);
    }

    // ════════════════════════════════════════════════════════
    // GET — By explicit business type (no JWT required)
    // ════════════════════════════════════════════════════════

    @Override
    public ApiResponse<List<CategorySuggestionResponse>> getCategoriesByBusinessType(String businessType) {
        List<CategorySuggestionResponse> list = catSuggRepo
                .findByBusinessTypeOrderByDisplayOrderAsc(businessType)
                .stream()
                .map(this::toCatResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Category suggestions loaded for " + businessType, list);
    }

    @Override
    public ApiResponse<List<ItemSuggestionResponse>> getItemsByBusinessTypeAndCategory(String businessType, String categoryName) {
        List<ItemSuggestionResponse> list = itemSuggRepo
                .findByBusinessTypeAndCategoryNameOrderByDisplayOrderAsc(businessType, categoryName)
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Item suggestions loaded", list);
    }

    // ════════════════════════════════════════════════════════
    // POST — Add new suggestion
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<CategorySuggestionResponse> addCategorySuggestion(CategorySuggestionRequest request) {
        CategorySuggestion suggestion = CategorySuggestion.builder()
                .businessType(request.getBusinessType().toLowerCase().trim())
                .categoryName(request.getCategoryName().trim())
                .categoryEmoji(request.getCategoryEmoji())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 99)
                .build();
        CategorySuggestion saved = catSuggRepo.save(suggestion);
        log.info("Added category suggestion id={} for businessType={}", saved.getId(), saved.getBusinessType());
        return ApiResponse.success("Category suggestion added successfully", toCatResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<ItemSuggestionResponse> addItemSuggestion(ItemSuggestionRequest request) {
        ItemSuggestion suggestion = ItemSuggestion.builder()
                .businessType(request.getBusinessType().toLowerCase().trim())
                .categoryName(request.getCategoryName().trim())
                .itemName(request.getItemName().trim())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 99)
                .build();
        ItemSuggestion saved = itemSuggRepo.save(suggestion);
        log.info("Added item suggestion id={} for businessType={}, category={}", saved.getId(), saved.getBusinessType(), saved.getCategoryName());
        return ApiResponse.success("Item suggestion added successfully", toItemResponse(saved));
    }

    // ════════════════════════════════════════════════════════
    // PUT — Update suggestion
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<CategorySuggestionResponse> updateCategorySuggestion(Long id, CategorySuggestionRequest request) {
        CategorySuggestion suggestion = catSuggRepo.findById(id)
                .orElseThrow(() -> new AppException("Category suggestion not found with id: " + id, HttpStatus.NOT_FOUND));

        suggestion.setBusinessType(request.getBusinessType().toLowerCase().trim());
        suggestion.setCategoryName(request.getCategoryName().trim());
        if (request.getCategoryEmoji() != null) suggestion.setCategoryEmoji(request.getCategoryEmoji());
        if (request.getDisplayOrder() != null) suggestion.setDisplayOrder(request.getDisplayOrder());

        CategorySuggestion updated = catSuggRepo.save(suggestion);
        return ApiResponse.success("Category suggestion updated successfully", toCatResponse(updated));
    }

    @Override
    @Transactional
    public ApiResponse<ItemSuggestionResponse> updateItemSuggestion(Long id, ItemSuggestionRequest request) {
        ItemSuggestion suggestion = itemSuggRepo.findById(id)
                .orElseThrow(() -> new AppException("Item suggestion not found with id: " + id, HttpStatus.NOT_FOUND));

        suggestion.setBusinessType(request.getBusinessType().toLowerCase().trim());
        suggestion.setCategoryName(request.getCategoryName().trim());
        suggestion.setItemName(request.getItemName().trim());
        if (request.getDisplayOrder() != null) suggestion.setDisplayOrder(request.getDisplayOrder());

        ItemSuggestion updated = itemSuggRepo.save(suggestion);
        return ApiResponse.success("Item suggestion updated successfully", toItemResponse(updated));
    }

    // ════════════════════════════════════════════════════════
    // DELETE — Remove suggestion
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<String> deleteCategorySuggestion(Long id) {
        if (!catSuggRepo.existsById(id)) {
            throw new AppException("Category suggestion not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        catSuggRepo.deleteById(id);
        log.info("Deleted category suggestion id={}", id);
        return ApiResponse.success("Category suggestion deleted successfully");
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteItemSuggestion(Long id) {
        if (!itemSuggRepo.existsById(id)) {
            throw new AppException("Item suggestion not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        itemSuggRepo.deleteById(id);
        log.info("Deleted item suggestion id={}", id);
        return ApiResponse.success("Item suggestion deleted successfully");
    }

    // ════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════

    /**
     * Resolves the admin's business type from their business record.
     * The business type is stored as lowercase with hyphens (e.g. "restaurant", "coffee-shop")
     * matching exactly what the frontend stores during registration.
     */
    private String resolveBusinessType(String adminId) {
        if (adminId == null || adminId.isBlank()) {
            throw new AppException("Authentication required.", HttpStatus.UNAUTHORIZED);
        }
        return businessRepo.findByAdminId(adminId)
                .map(BusinessInformation::getBusinessType)
                .map(bt -> bt.toLowerCase().trim())
                .orElse("restaurant"); // fallback default
    }

    private CategorySuggestionResponse toCatResponse(CategorySuggestion s) {
        return CategorySuggestionResponse.builder()
                .id(s.getId())
                .businessType(s.getBusinessType())
                .categoryName(s.getCategoryName())
                .categoryEmoji(s.getCategoryEmoji())
                .displayOrder(s.getDisplayOrder())
                .categoryImage(s.getCategoryImage())
                .categoryImageType(s.getCategoryImageType())
                .build();
    }

    private ItemSuggestionResponse toItemResponse(ItemSuggestion s) {
        return ItemSuggestionResponse.builder()
                .id(s.getId())
                .businessType(s.getBusinessType())
                .categoryName(s.getCategoryName())
                .itemName(s.getItemName())
                .displayOrder(s.getDisplayOrder())
                .itemImage(s.getItemImage())
                .itemImageType(s.getItemImageType())
                .build();
    }
}
