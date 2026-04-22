package com.ironhack.smarttourism.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
