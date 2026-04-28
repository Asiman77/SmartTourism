package com.ironhack.smarttourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String address;
    private String status;
    private Long userId;
    private String userFullName;
    private LocalDateTime createdAt;
}
