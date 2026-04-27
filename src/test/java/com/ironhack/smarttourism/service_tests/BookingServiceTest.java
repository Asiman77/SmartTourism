package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.config.SecurityUtils;
import com.ironhack.smarttourism.dto.request.BookingRequestDTO;
import com.ironhack.smarttourism.dto.response.BookingResponseDTO;
import com.ironhack.smarttourism.entity.Booking;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.BookingStatus;
import com.ironhack.smarttourism.mapper.BookingMapper;
import com.ironhack.smarttourism.repository.BookingRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import com.ironhack.smarttourism.repository.UserRepository;
import com.ironhack.smarttourism.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private TourPackageRepository tourPackageRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private BookingService bookingService;

    private User sampleUser;
    private TourPackage sampleTour;
    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);

        sampleTour = new TourPackage();
        sampleTour.setId(10L);
        sampleTour.setPrice(new BigDecimal("100.00"));

        sampleBooking = new Booking();
        sampleBooking.setId(100L);
        sampleBooking.setUser(sampleUser);
        sampleBooking.setTourPackage(sampleTour);
        sampleBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    void createBooking_ShouldSaveBookingSuccessfully() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId(1L);
        request.setTourPackageId(10L);
        request.setPersonsCount(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(sampleTour));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);
        when(bookingMapper.toResponseDTO(any())).thenReturn(new BookingResponseDTO());

        bookingService.createBooking(request);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void getMyBookings_ShouldReturnUserBookings() {
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(sampleBooking));

        bookingService.getMyBookings();

        verify(bookingRepository).findByUserId(1L);
    }

    @Test
    void cancelBooking_ShouldChangeStatusToCancelled() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);

        bookingService.cancelBooking(100L);

        assertEquals(BookingStatus.CANCELLED, sampleBooking.getStatus());
        verify(bookingRepository).save(sampleBooking);
    }

    @Test
    void cancelBooking_ShouldThrowException_WhenAlreadyCancelled() {
        sampleBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));

        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(100L));
    }

    @Test
    void getBookingById_ShouldReturnDto() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(sampleBooking));
        when(bookingMapper.toResponseDTO(sampleBooking)).thenReturn(new BookingResponseDTO());

        BookingResponseDTO result = bookingService.getBookingById(100L);

        assertNotNull(result);
        verify(bookingRepository).findById(100L);
    }
}