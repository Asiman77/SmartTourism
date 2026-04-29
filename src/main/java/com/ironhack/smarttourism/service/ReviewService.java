package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.ReviewRequestDTO;
import com.ironhack.smarttourism.dto.response.ReviewResponseDTO;
import com.ironhack.smarttourism.entity.Review;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.exception.ForbiddenException;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.mapper.ReviewMapper;
import com.ironhack.smarttourism.repository.BookingRepository;
import com.ironhack.smarttourism.repository.ReviewRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import com.ironhack.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        Long userId = securityUtils.getCurrentUserId();

        // Only the user that has booked can write review
        boolean hasBooking = bookingRepository.existsByUserIdAndTourPackageId(
                userId, dto.getTourPackageId()
        );
        if (!hasBooking) {
            throw new ForbiddenException("You can only review tours you have booked");
        }

        // Can't write review for the same tour twice
        boolean alreadyReviewed = reviewRepository.existsByUserIdAndTourPackageId(
                userId, dto.getTourPackageId()
        );
        if (alreadyReviewed) {
            throw new ForbiddenException("You have already reviewed this tour");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TourPackage tourPackage = tourPackageRepository.findById(dto.getTourPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setTourPackage(tourPackage);

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewRequestDTO dto) {
        Long userId = securityUtils.getCurrentUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().getId() != userId) {
            throw new ForbiddenException("You can only edit your own reviews");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Long userId = securityUtils.getCurrentUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().getId() != userId) {
            throw new ForbiddenException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByTour(Long tourPackageId) {
        if (!tourPackageRepository.existsById(tourPackageId)) {
            throw new ResourceNotFoundException("Tour not found");
        }

        return reviewRepository.findByTourPackageId(tourPackageId)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getMyReviews() {
        Long userId = securityUtils.getCurrentUserId();

        return reviewRepository.findByUserId(userId)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
}