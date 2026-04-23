package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.TourRequestDTO;
import com.ironhack.smarttourism.dto.request.TourStatusRequest;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService service;

    @PostMapping
    public ApiResponse<TourPackage> create(@RequestParam Long userId,
                                           @RequestBody @Valid TourRequestDTO dto) {
        return new ApiResponse<>(true, "Created", service.create(userId, dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<TourPackage> update(@RequestParam Long userId,
                                           @PathVariable Long id,
                                           @RequestBody TourRequestDTO dto) {
        return new ApiResponse<>(true, "Updated", service.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestParam Long userId,
                                    @PathVariable Long id) {
        service.delete(userId, id);
        return new ApiResponse<>(true, "Deleted", null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<TourPackage> status(@RequestParam Long userId,
                                           @PathVariable Long id,
                                           @RequestBody TourStatusRequest dto) {
        return new ApiResponse<>(true, "Status updated",
                service.changeStatus(userId, id, dto.getStatus()));
    }
}