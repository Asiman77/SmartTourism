package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.response.ReviewResponseDTO;
import com.ironhack.smarttourism.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDTO toResponse(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setTourPackageId(review.getTourPackage().getId());
        dto.setTourTitle(review.getTourPackage().getTitle());
        return dto;
    }
}