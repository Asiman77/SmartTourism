package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.entity.enums.TourStatus;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import com.ironhack.smarttourism.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private TourPackageRepository tourPackageRepository;

    @InjectMocks
    private AdminService adminService;

    private Agency sampleAgency;
    private TourPackage sampleTour;

    @BeforeEach
    void setUp() {
        sampleAgency = new Agency();
        sampleAgency.setId(1L);
        sampleAgency.setStatus(AgencyStatus.PENDING);

        sampleTour = new TourPackage();
        sampleTour.setId(10L);
        sampleTour.setStatus(TourStatus.INACTIVE);
    }

    @Test
    void getAllAgencies_shouldReturnList() {
        when(agencyRepository.findAll()).thenReturn(List.of(sampleAgency));

        List<Agency> result = adminService.getAllAgencies();

        assertEquals(1, result.size());
        verify(agencyRepository, times(1)).findAll();
    }

    @Test
    void approveAgency_shouldUpdateStatusToApproved() {
        when(agencyRepository.findById(1L)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenReturn(sampleAgency);

        Agency result = adminService.approveAgency(1L);

        assertEquals(AgencyStatus.APPROVED, result.getStatus());
        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void rejectAgency_shouldUpdateStatusToRejected() {
        when(agencyRepository.findById(1L)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenReturn(sampleAgency);

        Agency result = adminService.rejectAgency(1L);

        assertEquals(AgencyStatus.REJECTED, result.getStatus());
        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void suspendAgency_shouldUpdateStatusToSuspended() {
        when(agencyRepository.findById(1L)).thenReturn(Optional.of(sampleAgency));
        when(agencyRepository.save(any(Agency.class))).thenReturn(sampleAgency);

        Agency result = adminService.suspendAgency(1L);

        assertEquals(AgencyStatus.SUSPENDED, result.getStatus());
        verify(agencyRepository).save(sampleAgency);
    }

    @Test
    void changeTourStatus_shouldUpdateStatus() {
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(sampleTour));
        when(tourPackageRepository.save(any(TourPackage.class))).thenReturn(sampleTour);

        TourPackage result = adminService.changeTourStatus(10L, TourStatus.ACTIVE);

        assertEquals(TourStatus.ACTIVE, result.getStatus());
        verify(tourPackageRepository).save(sampleTour);
    }

    @Test
    void agencyNotFound_shouldThrowRuntimeException() {
        when(agencyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.approveAgency(99L));
    }

    @Test
    void tourNotFound_shouldThrowRuntimeException() {
        when(tourPackageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.changeTourStatus(99L, TourStatus.ACTIVE));
    }
}