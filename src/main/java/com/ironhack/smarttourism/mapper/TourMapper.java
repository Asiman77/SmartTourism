package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.response.TourResponseDTO;
import com.ironhack.smarttourism.dto.response.TourResponseDTO;
import com.ironhack.smarttourism.entity.TourPackage;
import org.springframework.stereotype.Component;

@Component
public class TourMapper {

    public TourResponseDTO toResponse(TourPackage tourPackage) {
        if (tourPackage == null) return null;

        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(tourPackage.getId());
        dto.setTitle(tourPackage.getTitle());
        dto.setDescription(tourPackage.getDescription());
        dto.setPrice(tourPackage.getPrice());
        dto.setCapacity(tourPackage.getCapacity());
        dto.setDurationDays(tourPackage.getDurationDays());
        dto.setMeetingPoint(tourPackage.getMeetingPoint());
        dto.setIncludedServices(tourPackage.getIncludedServices());
        dto.setExcludedServices(tourPackage.getExcludedServices());
        dto.setStatus(tourPackage.getStatus().name());

        dto.setAgencyId(tourPackage.getAgency().getId());
        dto.setAgencyName(tourPackage.getAgency().getName());

        dto.setCategoryId(tourPackage.getCategory().getId());
        dto.setCategoryName(tourPackage.getCategory().getName());

        dto.setDestinationId(tourPackage.getDestination().getId());
        dto.setDestinationName(tourPackage.getDestination().getName());
        dto.setDestinationCountry(tourPackage.getDestination().getCountry());

        dto.setCreatedAt(tourPackage.getCreatedAt());

        return dto;
    }
}