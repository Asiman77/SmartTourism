package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.BookingRequestDTO;
import com.ironhack.smarttourism.dto.response.BookingResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.Booking;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.BookingStatus;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.mapper.BookingMapper;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.BookingRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import com.ironhack.smarttourism.repository.UserRepository;
import com.ironhack.smarttourism.config.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TourPackageRepository tourPackageRepository;
    private final BookingMapper bookingMapper;
    private final SecurityUtils securityUtils;
    private final AgencyRepository agencyRepository;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + requestDTO.getUserId()));

        TourPackage tourPackage = tourPackageRepository.findById(requestDTO.getTourPackageId())
                .orElseThrow(() -> new RuntimeException("Tour package not found: " + requestDTO.getTourPackageId()));

        BigDecimal totalPrice = tourPackage.getPrice()
                .multiply(BigDecimal.valueOf(requestDTO.getPersonsCount()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTourPackage(tourPackage);
        booking.setPersonsCount(requestDTO.getPersonsCount());
        booking.setTotalPrice(totalPrice);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        return bookingMapper.toResponseDTO(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getMyBookings() {
        Long userId = securityUtils.getCurrentUserId();

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));

        return bookingMapper.toResponseDTO(booking);
    }

    @Transactional
    public BookingResponseDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Confirmed booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponseDTO(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByAgency() {
        Long userId = securityUtils.getCurrentUserId();

        Agency agency = agencyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency profile not found"));

        return bookingRepository.findByTourPackageAgencyId(agency.getId())
                .stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingByIdForAgency(Long bookingId) {
        Long userId = securityUtils.getCurrentUserId();

        Agency agency = agencyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency profile not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (booking.getTourPackage().getAgency().getId() != agency.getId()) {
            throw new AccessDeniedException("You do not have access to this booking");
        }

        return bookingMapper.toResponseDTO(booking);
    }
}