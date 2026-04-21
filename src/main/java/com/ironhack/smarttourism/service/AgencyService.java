package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.AgencyRequestDTO;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public Agency getAgencyProfileByUserId(Long userId) {
        return agencyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency profile not found"));
    }

    public Agency updateAgencyProfile(Long userId, AgencyRequestDTO request) {
        Agency agency = agencyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency profile not found"));

        agency.setName(request.getName());
        agency.setDescription(request.getDescription());
        agency.setEmail(request.getEmail());
        agency.setPhone(request.getPhone());
        agency.setAddress(request.getAddress());

        return agencyRepository.save(agency);
    }

    public AgencyStatus getAgencyStatusByUserId(Long userId) {
        Agency agency = agencyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency profile not found"));

        return agency.getStatus();
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public List<Agency> getAgenciesByStatus(AgencyStatus status) {
        return agencyRepository.findByStatus(status);
    }

    public Agency approveAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        agency.setStatus(AgencyStatus.APPROVED);
        return agencyRepository.save(agency);
    }

    public Agency rejectAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        agency.setStatus(AgencyStatus.REJECTED);
        return agencyRepository.save(agency);
    }

    public Agency suspendAgency(Long agencyId) {
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agency not found"));

        agency.setStatus(AgencyStatus.SUSPENDED);
        return agencyRepository.save(agency);
    }
}