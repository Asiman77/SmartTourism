package com.ironhack.smarttourism.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDTO {

    private Long id;
    private Integer rating;
    private String comment;
    private Long userId;
    private String username;
    private Long tourPackageId;
    private String tourTitle;
}