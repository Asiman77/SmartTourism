package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.BookingController;
import com.ironhack.smarttourism.dto.request.BookingRequestDTO;
import com.ironhack.smarttourism.dto.response.BookingResponseDTO;
import com.ironhack.smarttourism.entity.enums.BookingStatus;
import com.ironhack.smarttourism.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void createBooking_ShouldReturnCreated() throws Exception {
        //fills request
        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId(1L);
        request.setTourPackageId(10L);
        request.setPersonsCount(2);

        //fills response
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(100L);
        response.setStatus(BookingStatus.PENDING);
        response.setTourPackageName("Qabala Tour");

        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.tourPackageName").value("Qabala Tour"));
    }

    @Test
    void cancelBooking_ShouldReturnCancelled() throws Exception {
        Long bookingId = 1L;
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(bookingId);
        response.setStatus(BookingStatus.CANCELLED);

        when(bookingService.cancelBooking(bookingId)).thenReturn(response);

        mockMvc.perform(patch("/api/bookings/" + bookingId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getMyBookings_ShouldReturnList() throws Exception {
        BookingResponseDTO b1 = new BookingResponseDTO();
        b1.setId(1L);
        b1.setStatus(BookingStatus.CONFIRMED);

        when(bookingService.getMyBookings()).thenReturn(List.of(b1));

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        Long id = 5L;
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(id);
        response.setStatus(BookingStatus.COMPLETED);

        when(bookingService.getBookingById(id)).thenReturn(response);

        mockMvc.perform(get("/api/bookings/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}