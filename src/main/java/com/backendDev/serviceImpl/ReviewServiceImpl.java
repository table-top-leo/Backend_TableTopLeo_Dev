package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.*;
import com.backendDev.model.AppReview;
import com.backendDev.model.BusinessInformation;
import com.backendDev.model.BusinessReview;
import com.backendDev.repo.AppReviewRepository;
import com.backendDev.repo.BusinessInformationRepository;
import com.backendDev.repo.BusinessReviewRepository;
import com.backendDev.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final BusinessReviewRepository      businessReviewRepo;
    private final AppReviewRepository           appReviewRepo;
    private final BusinessInformationRepository businessRepo;

    // ── CUSTOMER → BUSINESS REVIEW ───────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<BusinessReviewResponse> submitBusinessReview(SubmitBusinessReviewRequest request) {
        BusinessInformation business = businessRepo.findByBusinessId(request.getBusinessId())
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        String phone = request.getCustomerPhone().trim();

        if (businessReviewRepo.existsByBusinessIdAndCustomerPhone(request.getBusinessId(), phone)) {
            throw new AppException("You have already reviewed this restaurant.", HttpStatus.CONFLICT);
        }

        BusinessReview review = BusinessReview.builder()
                .reviewId("BREV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                .businessId(business.getBusinessId())
                .adminId(business.getAdminId())
                .customerName(request.getCustomerName())
                .customerPhone(phone)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .build();

        businessReviewRepo.save(review);
        log.info("Business review {} submitted for business: {}", review.getReviewId(), business.getBusinessId());

        return ApiResponse.success("Review submitted successfully", toBusinessReviewResponse(review));
    }

    @Override
    public ApiResponse<List<BusinessReviewResponse>> getBusinessReviews(String businessId) {
        businessRepo.findByBusinessId(businessId)
                .orElseThrow(() -> new AppException("Business not found.", HttpStatus.NOT_FOUND));

        List<BusinessReviewResponse> reviews = businessReviewRepo
                .findAllByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::toBusinessReviewResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Reviews fetched successfully", reviews);
    }

    // ── MERCHANT/ADMIN → APPLICATION REVIEW ──────────────────────────
    @Override
    @Transactional
    public ApiResponse<AppReviewResponse> submitAppReview(SubmitAppReviewRequest request) {
        String adminId = UserContext.getAdminId();

        if (appReviewRepo.existsByAdminId(adminId)) {
            throw new AppException("You have already submitted a review for the application.", HttpStatus.CONFLICT);
        }

        String businessId = businessRepo.findByAdminId(adminId)
                .map(BusinessInformation::getBusinessId)
                .orElse(null);

        AppReview review = AppReview.builder()
                .reviewId("AREV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                .adminId(adminId)
                .businessId(businessId)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .build();

        appReviewRepo.save(review);
        log.info("App review {} submitted by admin: {}", review.getReviewId(), adminId);

        return ApiResponse.success("Thank you for your feedback!", toAppReviewResponse(review));
    }

    @Override
    public ApiResponse<AppReviewResponse> getMyAppReview() {
        String adminId = UserContext.getAdminId();
        AppReview review = appReviewRepo.findByAdminId(adminId)
                .orElseThrow(() -> new AppException("You haven't reviewed the application yet.", HttpStatus.NOT_FOUND));

        return ApiResponse.success("Review fetched successfully", toAppReviewResponse(review));
    }

    // ── MAPPERS ───────────────────────────────────────────────────────
    private BusinessReviewResponse toBusinessReviewResponse(BusinessReview r) {
        return BusinessReviewResponse.builder()
                .reviewId(r.getReviewId())
                .businessId(r.getBusinessId())
                .customerName(r.getCustomerName())
                .rating(r.getRating())
                .reviewText(r.getReviewText())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private AppReviewResponse toAppReviewResponse(AppReview r) {
        return AppReviewResponse.builder()
                .reviewId(r.getReviewId())
                .adminId(r.getAdminId())
                .rating(r.getRating())
                .reviewText(r.getReviewText())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
