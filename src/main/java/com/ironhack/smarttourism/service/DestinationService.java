package com.ironhack.smarttourism.service;


import com.ironhack.smarttourism.dto.request.DestinationRequestDTO;
import com.ironhack.smarttourism.dto.response.DestinationResponseDTO;
import com.ironhack.smarttourism.entity.Destination;
import com.ironhack.smarttourism.entity.enums.Season;
import com.ironhack.smarttourism.mapper.DestinationMapper;
import com.ironhack.smarttourism.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



//test
@Service
@RequiredArgsConstructor
public class DestinationService {
    private final DestinationRepository destinationRepository;

    public DestinationResponseDTO createDestination(DestinationRequestDTO destinationRequestDTO){
        Destination destination = new Destination();
        destination.setName(destinationRequestDTO.getName());
        destination.setCountry(destinationRequestDTO.getCountry());
        destination.setDescription(destinationRequestDTO.getDescription());
        destination.setSeason(destinationRequestDTO.getSeason());

        return DestinationMapper.toResponse(destinationRepository.save(destination));
    }

    public DestinationResponseDTO updateDestination(Long id , DestinationRequestDTO destinationRequestDTO){
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        destination.setName(destinationRequestDTO.getName());
        destination.setSeason(destinationRequestDTO.getSeason());
        destination.setCountry(destinationRequestDTO.getCountry());
        destination.setDescription(destinationRequestDTO.getDescription());

        return DestinationMapper.toResponse(destinationRepository.save(destination));
    }

    public DestinationResponseDTO getDestinationById(Long id){
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destiantion not found"));

        return DestinationMapper.toResponse(destinationRepository.save(destination));
    }

    public void deleteDestination(Long id){
        Destination destination = destinationRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Destination not found"));

        destinationRepository.delete(destination);
    }

    public List<DestinationResponseDTO> getAllDestinations() {
        return destinationRepository.findAll()
                .stream()
                .map(DestinationMapper::toResponse)
                .toList();
    }

    public List<DestinationResponseDTO> getDestinationsBySeason(Season season) {
        return destinationRepository.findBySeason(season)
                .stream()
                .map(DestinationMapper::toResponse)
                .toList();
    }

    public List<DestinationResponseDTO> getDestinationsByCountry(String country) {
        return destinationRepository.findByCountry(country)
                .stream()
                .map(DestinationMapper::toResponse)
                .toList();
    }
}
