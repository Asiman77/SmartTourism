package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.DestinationRequestDTO;
import com.ironhack.smarttourism.dto.response.DestinationResponseDTO;
import com.ironhack.smarttourism.entity.Destination;
import com.ironhack.smarttourism.entity.enums.Season;
import com.ironhack.smarttourism.repository.DestinationRepository;
import com.ironhack.smarttourism.service.DestinationService;
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
class DestinationServiceTest {

    @Mock
    private DestinationRepository destinationRepository;

    @InjectMocks
    private DestinationService destinationService;

    private Destination sampleDestination;
    private final Long destinationId = 1L;

    @BeforeEach
    void setUp() {
        sampleDestination = new Destination();
        sampleDestination.setId(destinationId);
        sampleDestination.setName("Baku");
        sampleDestination.setCountry("Azerbaijan");
        sampleDestination.setDescription("Beautiful city");
        sampleDestination.setSeason(Season.SUMMER);
    }

    @Test
    void createDestination_ShouldSaveDestination() {
        DestinationRequestDTO dto = new DestinationRequestDTO();
        dto.setName("Paris");
        dto.setCountry("France");
        dto.setDescription("Romantic city");
        dto.setSeason(Season.SPRING);

        when(destinationRepository.save(any(Destination.class)))
                .thenAnswer(invocation -> {
                    Destination destination = invocation.getArgument(0);
                    destination.setId(1L);
                    return destination;
                });

        DestinationResponseDTO result = destinationService.createDestination(dto);

        assertNotNull(result);
        assertEquals("Paris", result.getName());
        assertEquals("France", result.getCountry());
        assertEquals("Romantic city", result.getDescription());
        assertEquals(Season.SPRING, result.getSeason());

        verify(destinationRepository).save(any(Destination.class));
    }

    @Test
    void updateDestination_ShouldUpdateFieldsCorrectly() {
        DestinationRequestDTO dto = new DestinationRequestDTO();
        dto.setName("Updated Baku");
        dto.setCountry("Azerbaijan");
        dto.setDescription("Updated description");
        dto.setSeason(Season.WINTER);

        when(destinationRepository.findById(destinationId))
                .thenReturn(Optional.of(sampleDestination));

        when(destinationRepository.save(any(Destination.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DestinationResponseDTO result = destinationService.updateDestination(destinationId, dto);

        assertEquals("Updated Baku", result.getName());
        assertEquals("Azerbaijan", result.getCountry());
        assertEquals("Updated description", result.getDescription());
        assertEquals(Season.WINTER, result.getSeason());

        verify(destinationRepository).findById(destinationId);
        verify(destinationRepository).save(sampleDestination);
    }

    @Test
    void updateDestination_ShouldThrowException_WhenDestinationNotFound() {
        DestinationRequestDTO dto = new DestinationRequestDTO();

        when(destinationRepository.findById(destinationId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> destinationService.updateDestination(destinationId, dto)
        );

        assertEquals("Destination not found", exception.getMessage());

        verify(destinationRepository, never()).save(any(Destination.class));
    }

    @Test
    void getDestinationById_ShouldReturnDestination() {
        when(destinationRepository.findById(destinationId))
                .thenReturn(Optional.of(sampleDestination));

        when(destinationRepository.save(any(Destination.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DestinationResponseDTO result = destinationService.getDestinationById(destinationId);

        assertNotNull(result);
        assertEquals("Baku", result.getName());
        assertEquals("Azerbaijan", result.getCountry());

        verify(destinationRepository).findById(destinationId);
    }

    @Test
    void deleteDestination_ShouldDeleteDestination() {
        when(destinationRepository.findById(destinationId))
                .thenReturn(Optional.of(sampleDestination));

        destinationService.deleteDestination(destinationId);

        verify(destinationRepository).findById(destinationId);
        verify(destinationRepository).delete(sampleDestination);
    }

    @Test
    void deleteDestination_ShouldThrowException_WhenDestinationNotFound() {
        when(destinationRepository.findById(destinationId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> destinationService.deleteDestination(destinationId)
        );

        assertEquals("Destination not found", exception.getMessage());

        verify(destinationRepository, never()).delete(any(Destination.class));
    }

    @Test
    void getAllDestinations_ShouldReturnList() {
        when(destinationRepository.findAll())
                .thenReturn(List.of(sampleDestination));

        List<DestinationResponseDTO> result = destinationService.getAllDestinations();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Baku", result.get(0).getName());

        verify(destinationRepository).findAll();
    }

    @Test
    void getDestinationsBySeason_ShouldReturnList() {
        when(destinationRepository.findBySeason(Season.SUMMER))
                .thenReturn(List.of(sampleDestination));

        List<DestinationResponseDTO> result =
                destinationService.getDestinationsBySeason(Season.SUMMER);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(Season.SUMMER, result.get(0).getSeason());

        verify(destinationRepository).findBySeason(Season.SUMMER);
    }

    @Test
    void getDestinationsByCountry_ShouldReturnList() {
        when(destinationRepository.findByCountry("Azerbaijan"))
                .thenReturn(List.of(sampleDestination));

        List<DestinationResponseDTO> result =
                destinationService.getDestinationsByCountry("Azerbaijan");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Azerbaijan", result.get(0).getCountry());

        verify(destinationRepository).findByCountry("Azerbaijan");
    }
}