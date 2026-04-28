package com.ironhack.smarttourism.controller_tests;


import com.ironhack.smarttourism.controller.AdminAgencyController;
import com.ironhack.smarttourism.dto.response.AgencyResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.mapper.AgencyMapper;
import com.ironhack.smarttourism.service.AgencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class AdminAgencyControllerTest {


    private MockMvc mockMvc;


    @Mock
    private AgencyService agencyService;


    @Mock
    private AgencyMapper agencyMapper;


    @InjectMocks
    private AdminAgencyController adminAgencyController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminAgencyController).build();
    }


    @Test
    void updateStatus_shouldReturnUpdatedAgency() throws Exception {
        Long id = 1L;
        Agency agency = new Agency();
        AgencyResponseDTO responseDTO = new AgencyResponseDTO();
        responseDTO.setStatus("APPROVED");


        when(agencyService.updateAgencyStatus(eq(id), any(AgencyStatus.class))).thenReturn(agency);
        when(agencyMapper.toResponse(any())).thenReturn(responseDTO);


        mockMvc.perform(patch("/api/admin/agencies/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"APPROVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
