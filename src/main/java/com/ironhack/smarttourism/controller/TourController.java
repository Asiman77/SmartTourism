package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.TourRequestDTO;
import com.ironhack.smarttourism.dto.request.TourStatusRequest;
import com.ironhack.smarttourism.dto.response.TourResponseDTO;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.mapper.TourMapper;
import com.ironhack.smarttourism.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


//modified put - > patch
@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService service;

    private final TourMapper tourMapper;

    @PostMapping
    public ApiResponse<TourResponseDTO> create(
            @RequestParam Long userId,
            @RequestBody @Valid TourRequestDTO dto
    ) {
        TourPackage tour = service.create(userId, dto);
        TourResponseDTO response = tourMapper.toResponse(tour);

        return new ApiResponse<>(true, "Created", response);
    }

    @PutMapping("/{id}")
    public ApiResponse<TourResponseDTO> update(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid TourRequestDTO dto
    ) {
        TourPackage updatedTour = service.update(userId, id, dto);
        TourResponseDTO response = tourMapper.toResponse(updatedTour);

        return new ApiResponse<>(true, "Updated", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestParam Long userId,
            @PathVariable Long id
    ) {
        service.delete(userId, id);
        return new ApiResponse<>(true, "Deleted", null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<TourResponseDTO> status(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid TourStatusRequest dto
    ) {
        TourPackage updatedTour = service.changeStatus(userId, id, dto.getStatus());
        TourResponseDTO response = tourMapper.toResponse(updatedTour);

        return new ApiResponse<>(true, "Status updated", response);
    }
}