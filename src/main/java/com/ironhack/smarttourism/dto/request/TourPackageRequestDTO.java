package com.ironhack.smarttourism.dto.request;

import com.ironhack.smarttourism.entity.enums.TourStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Duration days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @Size(max = 255)
    private String meetingPoint;

    private String includedServices;

    private String excludedServices;

    @NotNull(message = "Agency ID is required")
    private Long agencyId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Destination ID is required")
    private Long destinationId;
}
