package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.TourController;
import com.ironhack.smarttourism.dto.request.TourRequestDTO;
import com.ironhack.smarttourism.dto.request.TourStatusRequest;
import com.ironhack.smarttourism.dto.response.TourResponseDTO;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.mapper.TourMapper;
import com.ironhack.smarttourism.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@ExtendWith(MockitoExtension.class)
class TourControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TourService service;

    @Mock
    private TourMapper tourMapper;

    @InjectMocks
    private TourController tourController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(tourController).build();
    }

    @Test
    void create_ShouldReturnCreatedTour() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);

        TourRequestDTO request = new TourRequestDTO();
        request.setTitle("Summer Adventure");
        request.setPrice(new BigDecimal("500.00"));
        request.setCapacity(20);
        request.setDurationDays(5);
        request.setCategoryId(1L);
        request.setDestinationId(1L);
        request.setStartDate(tomorrow);
        request.setEndDate(nextWeek);

        TourResponseDTO response = new TourResponseDTO();
        response.setId(1L);
        response.setTitle("Summer Adventure");

        when(service.create(anyLong(), any(TourRequestDTO.class))).thenReturn(new TourPackage());
        when(tourMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/tours")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Summer Adventure"));
    }

    @Test
    void delete_ShouldReturnSuccess() throws Exception {
        doNothing().when(service).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/api/tours/{id}", 1L)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted"));
    }

    @Test
    void changeStatus_ShouldReturnUpdatedTour() throws Exception {
        TourStatusRequest statusRequest = new TourStatusRequest();
        statusRequest.setStatus("ACTIVE");

        TourResponseDTO response = new TourResponseDTO();
        response.setId(1L);
        response.setStatus("ACTIVE");

        when(service.changeStatus(anyLong(), anyLong(), anyString())).thenReturn(new TourPackage());
        when(tourMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(patch("/api/tours/{id}/status", 1L)
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void getAll_ShouldReturnList() throws Exception {
        TourResponseDTO response = new TourResponseDTO();
        response.setTitle("Baku Tour");

        when(service.getAll(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(new TourPackage()));
        when(tourMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(get("/api/tours")
                        .param("keyword", "Baku"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Baku Tour"));
    }
}
