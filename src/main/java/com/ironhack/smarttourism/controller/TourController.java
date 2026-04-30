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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        return new ApiResponse<>(true, "Created", tourMapper.toResponse(tour));
    }

    @PatchMapping("/{id}")
    public ApiResponse<TourResponseDTO> update(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid TourRequestDTO dto
    ) {
        TourPackage updated = service.update(userId, id, dto);
        return new ApiResponse<>(true, "Updated", tourMapper.toResponse(updated));
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
    public ApiResponse<TourResponseDTO> changeStatus(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody @Valid TourStatusRequest dto
    ) {
        TourPackage updated = service.changeStatus(userId, id, dto.getStatus());
        return new ApiResponse<>(true, "Status updated", tourMapper.toResponse(updated));
    }

    @GetMapping
    public ApiResponse<List<TourResponseDTO>> getAll(
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TourResponseDTO> response = service.getAll(
                destinationId,
                categoryId,
                minPrice,
                maxPrice,
                status,
                keyword
        ).stream().map(tourMapper::toResponse).toList();

        return new ApiResponse<>(true, "Tours fetched", response);
    }
}