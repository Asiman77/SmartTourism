package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Tour Package ID is required")
    private Long tourPackageId;

    @NotNull(message = "Persons count is required")
    @Min(value = 1, message = "At least 1 person required")
    private Integer personsCount;

}
