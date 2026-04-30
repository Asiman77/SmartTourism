package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TourRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    @Min(1)
    private Integer capacity;

    @NotNull(message = "Duration is required")
    @Min(1)
    private Integer durationDays;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String meetingPoint;

    private String includedServices;

    private String excludedServices;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Destination is required")
    private Long destinationId;
}