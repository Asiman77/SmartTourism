package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.DestinationController;
import com.ironhack.smarttourism.dto.request.DestinationRequestDTO;
import com.ironhack.smarttourism.dto.response.DestinationResponseDTO;
import com.ironhack.smarttourism.entity.enums.Season;
import com.ironhack.smarttourism.service.DestinationService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DestinationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DestinationService destinationService;

    @InjectMocks
    private DestinationController destinationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(destinationController).build();
    }

    @Test
    void getAllDestinations_ShouldReturnList() throws Exception {
        DestinationResponseDTO destination = new DestinationResponseDTO();
        destination.setId(1L);
        destination.setName("Baku");

        when(destinationService.getAllDestinations()).thenReturn(List.of(destination));

        mockMvc.perform(get("/api/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Baku"));
    }

    @Test
    void getDestinationsByCountry_ShouldReturnFilteredList() throws Exception {
        String country = "Azerbaijan";
        DestinationResponseDTO destination = new DestinationResponseDTO();
        destination.setCountry(country);

        when(destinationService.getDestinationsByCountry(country)).thenReturn(List.of(destination));

        mockMvc.perform(get("/api/destinations/country/" + country))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value(country));
    }

    @Test
    void getDestinationBySeason_ShouldReturnFilteredList() throws Exception {
        Season season = Season.SUMMER;
        DestinationResponseDTO destination = new DestinationResponseDTO();
        destination.setSeason(season);

        when(destinationService.getDestinationsBySeason(season)).thenReturn(List.of(destination));

        mockMvc.perform(get("/api/destinations/season/" + season))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].season").value("SUMMER"));
    }

    @Test
    void createDestination_ShouldReturnCreated() throws Exception {
        DestinationRequestDTO request = new DestinationRequestDTO();
        request.setName("Gabala");

        DestinationResponseDTO response = new DestinationResponseDTO();
        response.setId(10L);
        response.setName("Gabala");

        when(destinationService.createDestination(any(DestinationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Gabala"));
    }

    @Test
    void deleteDestination_ShouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(destinationService).deleteDestination(any(Long.class));

        mockMvc.perform(delete("/api/destinations/{id}",id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateDestination_ShouldReturnUpdatedResponse() throws Exception {
        Long id = 1L;
        DestinationRequestDTO request = new DestinationRequestDTO();
        request.setName("New Name");

        DestinationResponseDTO response = new DestinationResponseDTO();
        response.setId(id);
        response.setName("New Name");

        when(destinationService.updateDestination(eq(id), any(DestinationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/destinations/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }
}