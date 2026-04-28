package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.TourRequestDTO;
import com.ironhack.smarttourism.entity.*;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.exception.ForbiddenException;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.CategoryRepository;
import com.ironhack.smarttourism.repository.DestinationRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import com.ironhack.smarttourism.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

//not sure of it
@ExtendWith(MockitoExtension.class)
class TourServiceTest {

    @Mock private TourPackageRepository tourRepo;
    @Mock private AgencyRepository agencyRepo;
    @Mock private CategoryRepository categoryRepo;
    @Mock private DestinationRepository destinationRepo;

    @InjectMocks
    private TourService tourService;

    private Agency sampleAgency;
    private TourPackage sampleTour;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        sampleAgency = new Agency();
        sampleAgency.setUser(user);
        sampleAgency.setStatus(AgencyStatus.APPROVED);

        sampleTour = new TourPackage();
        sampleTour.setAgency(sampleAgency);
    }

    @Test
    void create_ShouldWork() {
        TourRequestDTO dto = new TourRequestDTO();
        dto.setCategoryId(1L);
        dto.setDestinationId(1L);

        when(agencyRepo.findByUserId(1L)).thenReturn(Optional.of(sampleAgency));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(new Category()));
        when(destinationRepo.findById(1L)).thenReturn(Optional.of(new Destination()));
        when(tourRepo.save(any(TourPackage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        tourService.create(1L, dto);

        verify(tourRepo).save(any(TourPackage.class));
    }

    @Test
    void update_ShouldThrowException_WhenNotOwner() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(sampleTour));

        assertThrows(ForbiddenException.class, () -> tourService.update(99L, 1L, new TourRequestDTO()));
    }

    @Test
    void delete_ShouldWork() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(sampleTour));

        tourService.delete(1L, 1L);

        verify(tourRepo).delete(any(TourPackage.class));
    }

    @Test
    void changeStatus_ShouldWork() {
        when(tourRepo.findById(anyLong())).thenReturn(Optional.of(sampleTour));

        tourService.changeStatus(1L, 1L, "ACTIVE");

        verify(tourRepo).save(any(TourPackage.class));
    }

    @Test
    void getAll_ShouldWork() {
        tourService.getAll(anyLong(), anyLong(), any(), any(), anyString(), anyString());

        verify(tourRepo).filterTours(any(), any(), any(), any(), any(), any());
    }
}