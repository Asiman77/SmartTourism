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


    @GetMapping("/profile/my")
    public ApiResponse<AgencyResponseDTO> getMyProfile() {
        Agency agency = agencyService.getMyAgencyProfile();

        return new ApiResponse<>(
                true,
                "Agency profile fetched successfully",
                agencyMapper.toResponse(agency)
        );
    }

    @PutMapping("/profile/my")
    public ApiResponse<AgencyResponseDTO> updateMyProfile(
            @RequestBody @Valid AgencyRequestDTO request) {

        Agency updated = agencyService.updateMyAgencyProfile(request);

        return new ApiResponse<>(
                true,
                "Agency profile updated successfully",
                agencyMapper.toResponse(updated)
        );
    }

    @GetMapping("/status/my")
    public ApiResponse<String> getMyStatus() {
        AgencyStatus status = agencyService.getMyAgencyStatus();

        return new ApiResponse<>(
                true,
                "Agency status fetched successfully",
                status.name()
        );
    }
}