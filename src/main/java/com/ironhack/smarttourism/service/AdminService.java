package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.entity.enums.TourStatus;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.TourPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AgencyRepository agencyRepository;
    private final TourPackageRepository tourPackageRepository;

    public AdminService(AgencyRepository agencyRepository,
                        TourPackageRepository tourPackageRepository) {
        this.agencyRepository = agencyRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public Agency approveAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        agency.setStatus(AgencyStatus.APPROVED);
        return agencyRepository.save(agency);
    }

    public Agency rejectAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        agency.setStatus(AgencyStatus.REJECTED);
        return agencyRepository.save(agency);
    }

    public Agency suspendAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        agency.setStatus(AgencyStatus.SUSPENDED);
        return agencyRepository.save(agency);
    }

    public TourPackage changeTourStatus(Long tourId, TourStatus status) {
        TourPackage tourPackage = tourPackageRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        tourPackage.setStatus(status);
        return tourPackageRepository.save(tourPackage);
    }
}