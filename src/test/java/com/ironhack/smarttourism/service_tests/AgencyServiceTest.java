package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.AgencyRequestDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.service.AgencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgencyServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @InjectMocks
    private AgencyService agencyService;

    private Agency sampleAgency;
    private final Long userId = 100L;
    private final Long agencyId = 1L;

    @BeforeEach
    void setUp() {
        sampleAgency = new Agency();
        sampleAgency.setId(agencyId);
        sampleAgency.setStatus(AgencyStatus.PENDING);
        sampleAgency.setName("Test Agency");
    }

    @Test
    void updateAgencyStatus_ShouldChangeStatusToGivenValue() {
        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(i -> i.getArguments()[0]);

        Agency result = agencyService.updateAgencyStatus(agencyId, AgencyStatus.APPROVED);

        assertEquals(AgencyStatus.APPROVED, result.getStatus());
        verify(agencyRepository).save(any(Agency.class));
    }

    @Test
    void getAgencyProfileByUserId_ShouldReturnAgency() {
        when(agencyRepository.findByUserId(userId)).thenReturn(Optional.of(sampleAgency));

        Agency result = agencyService.getAgencyProfileByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, userId);
        verify(agencyRepository).findByUserId(userId);
    }

    @Test
    void updateAgencyProfile_ShouldUpdateFieldsCorrectly() {
        AgencyRequestDTO request = new AgencyRequestDTO();
        request.setName("Updated Name");
        request.setEmail("updated@agency.com");

        when(agencyRepository.findByUserId(userId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(i -> i.getArguments()[0]);

        Agency updated = agencyService.updateAgencyProfile(userId, request);

        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@agency.com", updated.getEmail());
    }

    @Test
    void getAgenciesByStatus_ShouldReturnList() {
        when(agencyRepository.findByStatus(AgencyStatus.APPROVED)).thenReturn(List.of(sampleAgency));

        List<Agency> result = agencyService.getAgenciesByStatus(AgencyStatus.APPROVED);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void approveAgency_ShouldSetStatusToApproved() {
        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(i -> i.getArguments()[0]);

        Agency result = agencyService.approveAgency(agencyId);

        assertEquals(AgencyStatus.APPROVED, result.getStatus());
    }
}