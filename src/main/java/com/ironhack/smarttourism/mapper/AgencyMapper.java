package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.response.AgencyResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import org.springframework.stereotype.Component;

@Component
public class AgencyMapper {

    public AgencyResponseDTO toResponse(Agency agency) {
        if (agency == null) return null;

        AgencyResponseDTO dto = new AgencyResponseDTO();
        dto.setId(agency.getId());
        dto.setName(agency.getName());
        dto.setDescription(agency.getDescription());
        dto.setEmail(agency.getEmail());
        dto.setPhone(agency.getPhone());
        dto.setAddress(agency.getAddress());
        dto.setStatus(agency.getStatus().name());

        return dto;
    }
}