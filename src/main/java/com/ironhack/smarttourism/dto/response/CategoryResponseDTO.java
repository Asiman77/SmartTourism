package com.ironhack.smarttourism.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
//sdgsdg

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
