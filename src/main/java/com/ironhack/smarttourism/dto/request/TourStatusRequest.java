package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TourStatusRequest {

    @NotBlank
    private String status; // ACTIVE / INACTIVE
}