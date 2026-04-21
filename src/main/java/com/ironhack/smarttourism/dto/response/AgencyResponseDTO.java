package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import lombok.*;
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
