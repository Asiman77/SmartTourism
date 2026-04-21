package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
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

    @GetMapping
    public ApiResponse<List<AgencyResponseDTO>> getAllAgencies() {
        List<AgencyResponseDTO> response = agencyService.getAllAgencies()
                .stream()
                .map(agencyMapper::toResponse)
                .toList();

        return new ApiResponse<>(true, "All agencies fetched successfully", response);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<AgencyResponseDTO>> getAgenciesByStatus(@PathVariable AgencyStatus status) {
        List<AgencyResponseDTO> response = agencyService.getAgenciesByStatus(status)
                .stream()
                .map(agencyMapper::toResponse)
                .toList();

        return new ApiResponse<>(true, "Filtered agencies fetched successfully", response);
    }

    @PatchMapping("/{agencyId}/approve")
    public ApiResponse<AgencyResponseDTO> approveAgency(@PathVariable Long agencyId) {
        Agency agency = agencyService.approveAgency(agencyId);
        return new ApiResponse<>(true, "Agency approved successfully", agencyMapper.toResponse(agency));
    }

    @PatchMapping("/{agencyId}/reject")
    public ApiResponse<AgencyResponseDTO> rejectAgency(@PathVariable Long agencyId) {
        Agency agency = agencyService.rejectAgency(agencyId);
        return new ApiResponse<>(true, "Agency rejected successfully", agencyMapper.toResponse(agency));
    }

    @PatchMapping("/{agencyId}/suspend")
    public ApiResponse<AgencyResponseDTO> suspendAgency(@PathVariable Long agencyId) {
        Agency agency = agencyService.suspendAgency(agencyId);
        return new ApiResponse<>(true, "Agency suspended successfully", agencyMapper.toResponse(agency));
    }
}