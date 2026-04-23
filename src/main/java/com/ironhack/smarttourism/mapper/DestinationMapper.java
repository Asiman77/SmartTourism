package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.response.DestinationResponseDTO;
import com.ironhack.smarttourism.entity.Destination;

public class DestinationMapper {

    public static DestinationResponseDTO toResponse(Destination destination){
        if(destination == null){
            return  null;
        }

        DestinationResponseDTO destinationResponseDTO = new DestinationResponseDTO();

        destinationResponseDTO.setId(destination.getId());
        destinationResponseDTO.setName(destination.getName());
        destinationResponseDTO.setCountry(destination.getCountry());
        destinationResponseDTO.setSeason(destination.getSeason());
        destinationResponseDTO.setDescription(destination.getDescription());

        return destinationResponseDTO;
    }
}
