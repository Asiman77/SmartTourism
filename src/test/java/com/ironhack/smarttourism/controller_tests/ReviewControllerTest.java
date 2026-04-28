package com.ironhack.smarttourism.controller_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.ReviewController;
import com.ironhack.smarttourism.dto.request.ReviewRequestDTO;
import com.ironhack.smarttourism.dto.response.ReviewResponseDTO;
import com.ironhack.smarttourism.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void createReview_ShouldReturnSuccessResponse() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setRating(5);
        request.setComment("Tour was perfect!");
        request.setTourPackageId(10L);

        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(1L);
        response.setRating(5);
        response.setComment("Tour was perfect!");

        when(reviewService.createReview(any(ReviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/reviews")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("$.message").value("Review created"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.rating").value(5));
    }
    @Test
    void getReviewsByTour_ShouldReturnList() throws Exception {
        Long tourId = 10L;
        ReviewResponseDTO review = new ReviewResponseDTO();
        review.setId(1L);
        review.setRating(4);

        when(reviewService.getReviewsByTour(tourId)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/tours/{id}/reviews", tourId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].rating").value(4));
    }

    @Test
    void deleteReview_ShouldReturnSuccess() throws Exception {
        Long reviewId = 1L;

        mockMvc.perform(delete("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Review deleted"))
                .andExpect(jsonPath("$.data").isEmpty()); //data is null in ApiResponse
    }

    @Test
    void updateReview_ShouldReturnUpdatedData() throws Exception {
        Long id = 1L;
        ReviewRequestDTO updateReq = new ReviewRequestDTO();
        updateReq.setRating(2);
        updateReq.setComment("Changed my mind");
        updateReq.setTourPackageId(10L);

        ReviewResponseDTO updatedRes = new ReviewResponseDTO();
        updatedRes.setId(id);
        updatedRes.setRating(2);

        when(reviewService.updateReview(eq(id), any(ReviewRequestDTO.class))).thenReturn(updatedRes);

        mockMvc.perform(put("/api/reviews/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(2))
                .andExpect(jsonPath("$.message").value("Review updated"));
    }
}
