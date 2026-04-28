package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.ReviewRequestDTO;
import com.ironhack.smarttourism.dto.response.ReviewResponseDTO;
import com.ironhack.smarttourism.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Tourist
    @PostMapping("/api/reviews")
    public ApiResponse<ReviewResponseDTO> createReview(
            @RequestBody @Valid ReviewRequestDTO dto) {

        return new ApiResponse<>(true, "Review created", reviewService.createReview(dto));
    }

    @PutMapping("/api/reviews/{id}")
    public ApiResponse<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewRequestDTO dto) {

        return new ApiResponse<>(true, "Review updated", reviewService.updateReview(id, dto));
    }

    @DeleteMapping("/api/reviews/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return new ApiResponse<>(true, "Review deleted", null);
    }

    // Public
    @GetMapping("/api/tours/{id}/reviews")
    public ApiResponse<List<ReviewResponseDTO>> getReviewsByTour(@PathVariable Long id) {
        return new ApiResponse<>(true, "Reviews fetched", reviewService.getReviewsByTour(id));
    }

    // User-specific
    @GetMapping("/api/reviews/my")
    public ApiResponse<List<ReviewResponseDTO>> getMyReviews() {
        return new ApiResponse<>(true, "My reviews fetched", reviewService.getMyReviews());
    }
}