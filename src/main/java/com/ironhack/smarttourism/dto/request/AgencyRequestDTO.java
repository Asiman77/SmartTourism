package com.ironhack.smarttourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
//2
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyRequestDTO {

    @NotBlank(message = "Agency name is required")
    @Size(max = 100)
    private String name;

    private String description;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 200)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;
}
