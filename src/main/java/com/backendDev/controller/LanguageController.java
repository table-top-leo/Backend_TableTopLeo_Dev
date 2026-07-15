package com.backendDev.controller;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.LanguageRequest;
import com.backendDev.dto.LanguageResponse;
import com.backendDev.model.User;
import com.backendDev.repo.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/language")
@RequiredArgsConstructor
public class LanguageController {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageController.class);
    private final UserRepository userRepository;

    // GET /api/admin/language — get current language for logged-in admin
    @GetMapping
    public ResponseEntity<ApiResponse<LanguageResponse>> getLanguage() {
        String adminId = UserContext.getAdminId();
        LOG.info("Fetching language preference for adminId: {}", adminId);
        User user = userRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException("Admin not found.", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.success("Language fetched", LanguageResponse.builder()
                .adminId(adminId)
                .languageCode(user.getLanguageCode() != null ? user.getLanguageCode() : "en")
                .languageName(user.getLanguageName() != null ? user.getLanguageName() : "English")
                .build()));
    }

    // PUT /api/admin/language — update language for logged-in admin
    @PutMapping
    @Transactional
    public ResponseEntity<ApiResponse<LanguageResponse>> updateLanguage(
            @Valid @RequestBody LanguageRequest request) {
        String adminId = UserContext.getAdminId();
        LOG.info("Updating language for adminId: {} to {}/{}", adminId, request.getLanguageCode(), request.getLanguageName());
        User user = userRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException("Admin not found.", HttpStatus.NOT_FOUND));
        user.setLanguageCode(request.getLanguageCode());
        user.setLanguageName(request.getLanguageName());
        userRepository.save(user);
        LOG.info("Language updated for adminId: {}", adminId);
        return ResponseEntity.ok(ApiResponse.success("Language updated successfully", LanguageResponse.builder()
                .adminId(adminId)
                .languageCode(request.getLanguageCode())
                .languageName(request.getLanguageName())
                .message("Language updated to " + request.getLanguageName())
                .build()));
    }

    // DELETE /api/admin/language — reset to English
    @DeleteMapping
    @Transactional
    public ResponseEntity<ApiResponse<LanguageResponse>> resetLanguage() {
        String adminId = UserContext.getAdminId();
        LOG.info("Resetting language to English for adminId: {}", adminId);
        User user = userRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException("Admin not found.", HttpStatus.NOT_FOUND));
        user.setLanguageCode("en");
        user.setLanguageName("English");
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Language reset to English", LanguageResponse.builder()
                .adminId(adminId).languageCode("en").languageName("English").build()));
    }
}
