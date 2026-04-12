package com.ironhack.smarttourism.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Long id;

    private Long userId;
    private String userFullName;

    private Long tourPackageId;
    private String tourPackageTitle;

    private Integer rating;
    private String comment;

    private LocalDateTime createdAt;
}
