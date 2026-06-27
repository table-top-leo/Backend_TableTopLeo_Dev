package com.backendDev.controller;

import com.backendDev.dto.*;
import com.backendDev.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;


    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategorySuggestionResponse>>> getCategorySuggestions() {
        return ResponseEntity.ok(suggestionService.getCategorySuggestions());
    }


    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ItemSuggestionResponse>>> getItemSuggestions(
            @RequestParam String categoryName) {
        return ResponseEntity.ok(suggestionService.getItemSuggestions(categoryName));
    }


    @GetMapping("/categories/public")
    public ResponseEntity<ApiResponse<List<CategorySuggestionResponse>>> getCategoriesByType(
            @RequestParam String businessType) {
        return ResponseEntity.ok(suggestionService.getCategoriesByBusinessType(businessType));
    }


    @GetMapping("/items/public")
    public ResponseEntity<ApiResponse<List<ItemSuggestionResponse>>> getItemsByTypeAndCategory(
            @RequestParam String businessType,
            @RequestParam String categoryName) {
        return ResponseEntity.ok(suggestionService.getItemsByBusinessTypeAndCategory(businessType, categoryName));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategorySuggestionResponse>> addCategorySuggestion(
            @Valid @RequestBody CategorySuggestionRequest request) {
        return ResponseEntity.ok(suggestionService.addCategorySuggestion(request));
    }


    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ItemSuggestionResponse>> addItemSuggestion(
            @Valid @RequestBody ItemSuggestionRequest request) {
        return ResponseEntity.ok(suggestionService.addItemSuggestion(request));
    }


    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategorySuggestionResponse>> updateCategorySuggestion(
            @PathVariable Long id,
            @Valid @RequestBody CategorySuggestionRequest request) {
        return ResponseEntity.ok(suggestionService.updateCategorySuggestion(id, request));
    }


    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemSuggestionResponse>> updateItemSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody ItemSuggestionRequest request) {
        return ResponseEntity.ok(suggestionService.updateItemSuggestion(id, request));
    }


    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategorySuggestion(@PathVariable Long id) {
        return ResponseEntity.ok(suggestionService.deleteCategorySuggestion(id));
    }


    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<String>> deleteItemSuggestion(@PathVariable Long id) {
        return ResponseEntity.ok(suggestionService.deleteItemSuggestion(id));
    }
}
