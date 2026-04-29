package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.service.SecurityUtils;
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
import com.ironhack.smarttourism.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourPackageRepository tourPackageRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private TourPackage tourPackage;
    private Review review;
    private ReviewRequestDTO dto;
    private ReviewResponseDTO responseDTO;

    private final Long userId = 1L;
    private final Long tourId = 10L;
    private final Long reviewId = 100L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        tourPackage = new TourPackage();
        tourPackage.setId(tourId);

        review = new Review();
        review.setId(reviewId);
        review.setRating(5);
        review.setComment("Great tour");
        review.setUser(user);
        review.setTourPackage(tourPackage);

        dto = new ReviewRequestDTO();
        dto.setTourPackageId(tourId);
        dto.setRating(5);
        dto.setComment("Great tour");

        responseDTO = new ReviewResponseDTO();
    }

    @Test
    void createReview_ShouldCreateReview() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(bookingRepository.existsByUserIdAndTourPackageId(userId, tourId)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndTourPackageId(userId, tourId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tourPackageRepository.findById(tourId)).thenReturn(Optional.of(tourPackage));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewMapper.toResponse(any(Review.class))).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.createReview(dto);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewMapper).toResponse(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowException_WhenUserHasNoBooking() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(bookingRepository.existsByUserIdAndTourPackageId(userId, tourId)).thenReturn(false);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.createReview(dto)
        );

        assertEquals("You can only review tours you have booked", exception.getMessage());

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowException_WhenAlreadyReviewed() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(bookingRepository.existsByUserIdAndTourPackageId(userId, tourId)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndTourPackageId(userId, tourId)).thenReturn(true);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.createReview(dto)
        );

        assertEquals("You have already reviewed this tour", exception.getMessage());

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldUpdateReview() {
        dto.setRating(4);
        dto.setComment("Updated comment");

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewMapper.toResponse(any(Review.class))).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.updateReview(reviewId, dto);

        assertNotNull(result);
        assertEquals(4, review.getRating());
        assertEquals("Updated comment", review.getComment());

        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.updateReview(reviewId, dto)
        );

        assertEquals("Review not found", exception.getMessage());
    }

    @Test
    void updateReview_ShouldThrowException_WhenNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        review.setUser(anotherUser);

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.updateReview(reviewId, dto)
        );

        assertEquals("You can only edit your own reviews", exception.getMessage());

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.deleteReview(reviewId)
        );

        assertEquals("Review not found", exception.getMessage());
    }

    @Test
    void deleteReview_ShouldThrowException_WhenNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        review.setUser(anotherUser);

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.deleteReview(reviewId)
        );

        assertEquals("You can only delete your own reviews", exception.getMessage());

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void getReviewsByTour_ShouldReturnList() {
        when(tourPackageRepository.existsById(tourId)).thenReturn(true);
        when(reviewRepository.findByTourPackageId(tourId)).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(responseDTO);

        List<ReviewResponseDTO> result = reviewService.getReviewsByTour(tourId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(reviewRepository).findByTourPackageId(tourId);
    }

    @Test
    void getReviewsByTour_ShouldThrowException_WhenTourNotFound() {
        when(tourPackageRepository.existsById(tourId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> reviewService.getReviewsByTour(tourId)
        );

        assertEquals("Tour not found", exception.getMessage());
    }

    @Test
    void getMyReviews_ShouldReturnList() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(reviewRepository.findByUserId(userId)).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(responseDTO);

        List<ReviewResponseDTO> result = reviewService.getMyReviews();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(reviewRepository).findByUserId(userId);
    }
}