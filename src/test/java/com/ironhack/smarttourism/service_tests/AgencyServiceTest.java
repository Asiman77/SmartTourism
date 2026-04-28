package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.config.SecurityUtils;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgencyServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private SecurityUtils securityUtils;

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
    void getMyAgencyProfile_ShouldReturnAgency() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(agencyRepository.findByUserId(userId)).thenReturn(Optional.of(sampleAgency));

        Agency result = agencyService.getMyAgencyProfile();

        assertNotNull(result);
        assertEquals("Test Agency", result.getName());

        verify(securityUtils).getCurrentUserId();
        verify(agencyRepository).findByUserId(userId);
    }

    @Test
    void updateMyAgencyProfile_ShouldUpdateFieldsCorrectly() {
        AgencyRequestDTO request = new AgencyRequestDTO();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setEmail("updated@agency.com");
        request.setPhone("+994501112233");
        request.setAddress("Baku, Azerbaijan");

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(agencyRepository.findByUserId(userId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agency updated = agencyService.updateMyAgencyProfile(request);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals("updated@agency.com", updated.getEmail());
        assertEquals("+994501112233", updated.getPhone());
        assertEquals("Baku, Azerbaijan", updated.getAddress());

        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void getMyAgencyStatus_ShouldReturnStatus() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(agencyRepository.findByUserId(userId)).thenReturn(Optional.of(sampleAgency));

        AgencyStatus status = agencyService.getMyAgencyStatus();

        assertEquals(AgencyStatus.PENDING, status);
    }

    @Test
    void getAllAgencies_ShouldReturnList() {
        when(agencyRepository.findAll()).thenReturn(List.of(sampleAgency));

        List<Agency> result = agencyService.getAllAgencies();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(agencyRepository).findAll();
    }

    @Test
    void getAgenciesByStatus_ShouldReturnList() {
        when(agencyRepository.findByStatus(AgencyStatus.APPROVED))
                .thenReturn(List.of(sampleAgency));

        List<Agency> result = agencyService.getAgenciesByStatus(AgencyStatus.APPROVED);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(agencyRepository).findByStatus(AgencyStatus.APPROVED);
    }

    @Test
    void updateAgencyStatus_ShouldChangeStatusToGivenValue() {
        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agency result = agencyService.updateAgencyStatus(agencyId, AgencyStatus.APPROVED);

        assertEquals(AgencyStatus.APPROVED, result.getStatus());

        verify(agencyRepository).findById(agencyId);
        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void rejectAgency_ShouldSetStatusToRejected() {
        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agency result = agencyService.rejectAgency(agencyId);

        assertEquals(AgencyStatus.REJECTED, result.getStatus());

        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void suspendAgency_ShouldSetStatusToSuspended() {
        when(agencyRepository.findById(agencyId)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Agency result = agencyService.suspendAgency(agencyId);

        assertEquals(AgencyStatus.SUSPENDED, result.getStatus());

        verify(agencyRepository).save(sampleAgency);
    }
}