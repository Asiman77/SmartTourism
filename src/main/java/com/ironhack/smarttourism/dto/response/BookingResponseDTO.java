package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.BookingStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
//sdgsdg

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    private Long id;

    private Long userId;
    private String userFullName;

    private Long tourPackageId;
    private String tourPackageTitle;
    private BigDecimal tourPackagePrice;

    private Integer personsCount;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private BookingStatus status;

    private LocalDateTime createdAt;
}
