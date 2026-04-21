package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.AgencyRequestDTO;
import com.ironhack.smarttourism.dto.response.AgencyResponseDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.mapper.AgencyMapper;
import com.ironhack.smarttourism.service.AgencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agency")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;
    private final AgencyMapper agencyMapper;

    @GetMapping("/profile/{userId}")
    public ApiResponse<AgencyResponseDTO> getAgencyProfile(@PathVariable Long userId) {
        Agency agency = agencyService.getAgencyProfileByUserId(userId);
        return new ApiResponse<>(true, "Agency profile fetched successfully", agencyMapper.toResponse(agency));
    }

    @PutMapping("/profile/{userId}")
    public ApiResponse<AgencyResponseDTO> updateAgencyProfile(@PathVariable Long userId,
                                                              @RequestBody @Valid AgencyRequestDTO request) {
        Agency updatedAgency = agencyService.updateAgencyProfile(userId, request);
        return new ApiResponse<>(true, "Agency profile updated successfully", agencyMapper.toResponse(updatedAgency));
    }

    @GetMapping("/status/{userId}")
    public ApiResponse<String> getAgencyStatus(@PathVariable Long userId) {
        AgencyStatus status = agencyService.getAgencyStatusByUserId(userId);
        return new ApiResponse<>(true, "Agency status fetched successfully", status.name());
    }
}