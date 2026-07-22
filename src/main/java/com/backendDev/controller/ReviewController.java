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

import java.util.List;

/**
 * Customer → Business/Merchant reviews. Public — no login for customers.
 */
@RestController
@RequestMapping("/api/reviews/business")
@RequiredArgsConstructor
public class ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    public static final String SUBMIT_REVIEW = "";
    public static final String GET_REVIEWS   = "/{businessId}";

    private final ReviewService reviewService;

    @PostMapping(value = SUBMIT_REVIEW, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<BusinessReviewResponse>> submitBusinessReview(
            @Valid @RequestBody SubmitBusinessReviewRequest request) {
        LOG.info("Submitting business review for businessId: {}", request.getBusinessId());
        ApiResponse<BusinessReviewResponse> response = reviewService.submitBusinessReview(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = GET_REVIEWS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BusinessReviewResponse>>> getBusinessReviews(
            @PathVariable String businessId) {
        LOG.info("Fetching reviews for businessId: {}", businessId);
        ApiResponse<List<BusinessReviewResponse>> response = reviewService.getBusinessReviews(businessId);
        return ResponseEntity.ok(response);
    }
}
