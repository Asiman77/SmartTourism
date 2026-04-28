package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 1000)
    private String comment;

    @NotNull
    private Long tourPackageId;
}