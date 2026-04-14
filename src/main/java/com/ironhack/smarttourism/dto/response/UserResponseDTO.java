package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.RoleName;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private RoleName role;
    private LocalDateTime createdAt;
}
