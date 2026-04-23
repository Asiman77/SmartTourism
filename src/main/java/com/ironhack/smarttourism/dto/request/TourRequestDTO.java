package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TourRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    @NotNull(message = "Duration is required")
    private Integer durationDays;

    private String meetingPoint;

    private String includedServices;

    private String excludedServices;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Destination is required")
    private Long destinationId;
}