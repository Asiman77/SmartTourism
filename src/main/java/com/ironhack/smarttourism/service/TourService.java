package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.TourRequestDTO;
import com.ironhack.smarttourism.entity.*;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.entity.enums.TourStatus;
import com.ironhack.smarttourism.exception.ForbiddenException;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourPackageRepository tourRepo;
    private final AgencyRepository agencyRepo;
    private final CategoryRepository categoryRepo;
    private final DestinationRepository destinationRepo;

    public TourPackage create(Long userId, TourRequestDTO dto) {

        Agency agency = agencyRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        if (agency.getStatus() != AgencyStatus.APPROVED) {
            throw new ForbiddenException("Agency not approved");
        }

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Destination destination = destinationRepo.findById(dto.getDestinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found"));

        TourPackage tour = new TourPackage();
        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setPrice(dto.getPrice());
        tour.setCapacity(dto.getCapacity());
        tour.setDurationDays(dto.getDurationDays());
        tour.setMeetingPoint(dto.getMeetingPoint());
        tour.setIncludedServices(dto.getIncludedServices());
        tour.setExcludedServices(dto.getExcludedServices());
        tour.setStatus(TourStatus.ACTIVE);
        tour.setAgency(agency);
        tour.setCategory(category);
        tour.setDestination(destination);

        return tourRepo.save(tour);
    }

    public TourPackage update(Long userId, Long id, TourRequestDTO dto) {

        TourPackage tour = tourRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        if (tour.getAgency().getUser().getId()!=(userId)) {
            throw new ForbiddenException("Not your tour");
        }

        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setPrice(dto.getPrice());
        tour.setCapacity(dto.getCapacity());
        tour.setDurationDays(dto.getDurationDays());

        return tourRepo.save(tour);
    }

    public void delete(Long userId, Long id) {

        TourPackage tour = tourRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        if (tour.getAgency().getUser().getId()!=(userId)) {
            throw new ForbiddenException("Not your tour");
        }

        tourRepo.delete(tour);
    }

    public TourPackage changeStatus(Long userId, Long id, String status) {

        TourPackage tour = tourRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        if (tour.getAgency().getUser().getId()!=(userId)) {
            throw new ForbiddenException("Not your tour");
        }

        tour.setStatus(TourStatus.valueOf(status));
        return tourRepo.save(tour);
    }

    // 🔥 FILTER LOGIC
    public List<TourPackage> getAll(
            Long destinationId,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status,
            String keyword
    ) {
        TourStatus tourStatus = null;

        if (status != null && !status.isBlank()) {
            tourStatus = TourStatus.valueOf(status.toUpperCase());
        }

        return tourRepo.filterTours(
                destinationId,
                categoryId,
                minPrice,
                maxPrice,
                tourStatus,
                keyword
        );
    }
}