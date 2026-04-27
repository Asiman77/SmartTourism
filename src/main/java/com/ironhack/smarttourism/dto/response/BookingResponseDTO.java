package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long tourPackageId;
    private String tourPackageName;
    private Integer personsCount;
    private BigDecimal totalPrice;
    private LocalDateTime bookingDate;
    private BookingStatus status;
}