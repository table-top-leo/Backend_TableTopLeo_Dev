package com.backendDev.controller;

import com.backendDev.dto.*;
import com.backendDev.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Merchant/Admin → Application reviews. JWT protected (falls under the
 * existing catch-all `.anyRequest().authenticated()` in SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/reviews/app")
@RequiredArgsConstructor
public class AdminReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminReviewController.class);

    public static final String SUBMIT_REVIEW = "";
    public static final String GET_MY_REVIEW = "/mine";

    private final ReviewService reviewService;

    @PostMapping(value = SUBMIT_REVIEW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AppReviewResponse>> submitAppReview(
            @Valid @RequestBody SubmitAppReviewRequest request) {
        LOG.info("Admin submitting application review");
        ApiResponse<AppReviewResponse> response = reviewService.submitAppReview(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = GET_MY_REVIEW, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AppReviewResponse>> getMyAppReview() {
        LOG.info("Admin fetching their own application review");
        ApiResponse<AppReviewResponse> response = reviewService.getMyAppReview();
        return ResponseEntity.ok(response);
    }
}
