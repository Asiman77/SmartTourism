package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
//sdgsdg

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

    private String description;
}
