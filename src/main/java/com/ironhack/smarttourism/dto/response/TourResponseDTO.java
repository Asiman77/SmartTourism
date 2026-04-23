package com.ironhack.smarttourism.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TourResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer capacity;
    private Integer durationDays;
    private String meetingPoint;
    private String includedServices;
    private String excludedServices;
    private String status;

    private Long agencyId;
    private String agencyName;

    private Long categoryId;
    private String categoryName;

    private Long destinationId;
    private String destinationName;
    private String destinationCountry;

    private LocalDateTime createdAt;
}