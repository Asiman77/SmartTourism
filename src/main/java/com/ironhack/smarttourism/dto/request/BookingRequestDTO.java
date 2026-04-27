package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Tour package ID is required")
    private Long tourPackageId;

    @NotNull(message = "Persons count is required")
    @Min(value = 1, message = "At least 1 person is required")
    private Integer personsCount;
}
