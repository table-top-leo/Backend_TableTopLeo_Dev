package com.backendDev.service;

import com.backendDev.dto.*;
import java.util.List;

/**
 * Suggestion Service — Category & Item smart suggestions based on business type.
 * GET endpoints use JWT to auto-resolve the admin's business type.
 * POST/PUT/DELETE endpoints are for admin management of suggestion master data.
 */
public interface SuggestionService {

    // ── GET (used by frontend menu page) ───────────────────
    ApiResponse<List<CategorySuggestionResponse>> getCategorySuggestions();
    ApiResponse<List<ItemSuggestionResponse>> getItemSuggestions(String categoryName);

    // ── GET by business type (no JWT needed) ───────────────
    ApiResponse<List<CategorySuggestionResponse>> getCategoriesByBusinessType(String businessType);
    ApiResponse<List<ItemSuggestionResponse>> getItemsByBusinessTypeAndCategory(String businessType, String categoryName);

    // ── POST (add new suggestion) ───────────────────────────
    ApiResponse<CategorySuggestionResponse> addCategorySuggestion(CategorySuggestionRequest request);
    ApiResponse<ItemSuggestionResponse> addItemSuggestion(ItemSuggestionRequest request);

    // ── PUT (update suggestion) ─────────────────────────────
    ApiResponse<CategorySuggestionResponse> updateCategorySuggestion(Long id, CategorySuggestionRequest request);
    ApiResponse<ItemSuggestionResponse> updateItemSuggestion(Long id, ItemSuggestionRequest request);

    // ── DELETE ──────────────────────────────────────────────
    ApiResponse<String> deleteCategorySuggestion(Long id);
    ApiResponse<String> deleteItemSuggestion(Long id);
}
