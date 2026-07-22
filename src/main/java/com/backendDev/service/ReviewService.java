package com.backendDev.service;

import com.backendDev.dto.*;

import java.util.List;

public interface ReviewService {

    // ── Customer → Business/Merchant review ─────────────────────────
    ApiResponse<BusinessReviewResponse>       submitBusinessReview(SubmitBusinessReviewRequest request);
    ApiResponse<List<BusinessReviewResponse>> getBusinessReviews(String businessId);

    // ── Merchant/Admin → Application review ─────────────────────────
    ApiResponse<AppReviewResponse> submitAppReview(SubmitAppReviewRequest request);
    ApiResponse<AppReviewResponse> getMyAppReview();
}
