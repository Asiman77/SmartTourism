package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.Season;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationResponseDTO {
//sdgsdg

    private Long id;
    private String name;
    private String country;
    private String description;
    private Season season;
    private LocalDateTime createdAt;
}
