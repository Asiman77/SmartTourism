package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.UpdateAgencyStatusRequest;
import com.ironhack.smarttourism.dto.response.AgencyResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.mapper.AgencyMapper;
import com.ironhack.smarttourism.service.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agencies")
@RequiredArgsConstructor
public class AdminController {

    private final AgencyService agencyService;
    private final AgencyMapper agencyMapper;

    @PatchMapping("/{id}/status")
    public ApiResponse<AgencyResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateAgencyStatusRequest request
    ) {
        AgencyStatus status = AgencyStatus.valueOf(request.status());
        var agency = agencyService.updateAgencyStatus(id, status);

        return new ApiResponse<>(true, "Updated", agencyMapper.toResponse(agency));
    }

    @GetMapping
    public ApiResponse<List<AgencyResponseDTO>> getAgencies(
            @RequestParam(required = false) AgencyStatus status
    ) {
        List<Agency> agencies;

        if (status != null) {
            agencies = agencyService.getAgenciesByStatus(status);
        } else {
            agencies = agencyService.getAllAgencies();
        }

        List<AgencyResponseDTO> response = agencies.stream()
                .map(agencyMapper::toResponse)
                .toList();

        return new ApiResponse<>(true, "Agencies fetched", response);
    }
}