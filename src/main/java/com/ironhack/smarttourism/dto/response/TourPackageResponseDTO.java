package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.TourStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer capacity;
    private Integer durationDays;
    private String meetingPoint;
    private String includedServices;
    private String excludedServices;
    private TourStatus status;

    private Long agencyId;
    private String agencyName;

    private Long categoryId;
    private String categoryName;

    private Long destinationId;
    private String destinationName;
    private String destinationCountry;

    private LocalDateTime createdAt;
}
