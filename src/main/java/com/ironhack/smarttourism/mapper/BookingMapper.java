package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.response.BookingResponseDTO;
import com.ironhack.smarttourism.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getUsername());
        dto.setTourPackageId(booking.getTourPackage().getId());
        dto.setTourPackageName(booking.getTourPackage().getTitle());
        dto.setPersonsCount(booking.getPersonsCount());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
