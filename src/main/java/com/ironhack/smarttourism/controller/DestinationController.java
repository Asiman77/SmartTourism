package com.ironhack.smarttourism.controller;


import com.ironhack.smarttourism.dto.request.DestinationRequestDTO;
import com.ironhack.smarttourism.dto.response.DestinationResponseDTO;
import com.ironhack.smarttourism.entity.enums.Season;
import com.ironhack.smarttourism.service.DestinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//Destination Controller
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
@RestController
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    List<DestinationResponseDTO> getAllDestinations(){
        return destinationService.getAllDestinations();
    }

    @GetMapping("/country/{country}")
    public List<DestinationResponseDTO> getDestinationsByCountry(@PathVariable String country) {
        return destinationService.getDestinationsByCountry(country);
    }

    @GetMapping("/season/{season}")
    public List<DestinationResponseDTO> getDestinationBySeason(@PathVariable Season season){
        return destinationService.getDestinationsBySeason(season);
    }

    @GetMapping("/{id}")
    public DestinationResponseDTO getDestinationById(@PathVariable Long id){
        return destinationService.getDestinationById(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DestinationResponseDTO createDestination(@RequestBody DestinationRequestDTO destinationRequestDTO){
        return destinationService.createDestination(destinationRequestDTO);
    }

    @PatchMapping("/{id}")
    public DestinationResponseDTO updateDestination(@PathVariable Long id,
                                                    @RequestBody DestinationRequestDTO destinationRequestDTO){
        return destinationService.updateDestination(id,destinationRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDestination(@PathVariable Long id){
        destinationService.deleteDestination(id);
    }


}
