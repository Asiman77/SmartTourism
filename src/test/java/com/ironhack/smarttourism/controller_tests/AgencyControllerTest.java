package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.AgencyController;
import com.ironhack.smarttourism.dto.request.AgencyRequestDTO;
import com.ironhack.smarttourism.dto.response.AgencyResponseDTO;
import com.ironhack.smarttourism.dto.response.BookingResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.mapper.AgencyMapper;
import com.ironhack.smarttourism.service.AgencyService;
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
class AgencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AgencyService agencyService;

    @Mock
    private AgencyMapper agencyMapper;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private AgencyController agencyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(agencyController).build();
    }

    @Test
    void getMyProfile_ShouldReturnProfile() throws Exception {
        Agency agency = new Agency();
        agency.setName("Global Travel");

        AgencyResponseDTO response = new AgencyResponseDTO();
        response.setName("Global Travel");

        when(agencyService.getMyAgencyProfile()).thenReturn(agency);
        when(agencyMapper.toResponse(agency)).thenReturn(response);

        mockMvc.perform(get("/api/agency/profile/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Global Travel"));
    }

    @Test
    void updateMyProfile_ShouldReturnUpdated() throws Exception {
        AgencyRequestDTO requestDTO = new AgencyRequestDTO();
        requestDTO.setName("Updated Agency");
        requestDTO.setEmail("test@agency.com"); // we added validation,so it is mandatory
        requestDTO.setAddress("Baku, Azerbaijan"); // we added validation,so it is mandatory
        requestDTO.setDescription("Nice agency");
        requestDTO.setPhone("123456789");

        Agency updatedAgency = new Agency();
        AgencyResponseDTO responseDTO = new AgencyResponseDTO();
        responseDTO.setName("Updated Agency");

        // any(AgencyRequestDTO.class) is more secure
        when(agencyService.updateMyAgencyProfile(any(AgencyRequestDTO.class))).thenReturn(updatedAgency);
        when(agencyMapper.toResponse(any())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/agency/profile/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Agency profile updated successfully"));
    }

    @Test
    void getMyStatus_ShouldReturnActiveStatus() throws Exception {
        when(agencyService.getMyAgencyStatus()).thenReturn(AgencyStatus.APPROVED);

        mockMvc.perform(get("/api/agency/status/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("APPROVED"));
    }

    @Test
    void getMyAgencyBookings_ShouldReturnList() throws Exception {
        BookingResponseDTO booking = new BookingResponseDTO();
        booking.setId(1L);
        List<BookingResponseDTO> bookings = List.of(booking);

        when(bookingService.getBookingsByAgency()).thenReturn(bookings);

        mockMvc.perform(get("/api/agency/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void getMyAgencyBookingById_ShouldReturnBooking() throws Exception {
        BookingResponseDTO booking = new BookingResponseDTO();
        booking.setId(5L);

        when(bookingService.getBookingByIdForAgency(5L)).thenReturn(booking);

        mockMvc.perform(get("/api/agency/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5));
    }
}