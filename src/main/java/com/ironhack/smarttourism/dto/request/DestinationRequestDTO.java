package com.ironhack.smarttourism.dto.request;

import com.ironhack.smarttourism.entity.enums.Season;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationRequestDTO {

    @NotBlank(message = "Destination name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 150)
    private String country;

    private String description;

    @NotNull(message = "Season is required")
    private Season season;
}
