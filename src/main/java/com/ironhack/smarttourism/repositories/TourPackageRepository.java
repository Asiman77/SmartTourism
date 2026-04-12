package com.ironhack.smarttourism.repositories;

import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface TourPackageRepository extends JpaRepository <TourPackage,Long> {
    List<TourPackage> findByAgencyId (Long agencyId);
    List<TourPackage>findByDestinationId (Long destinationId);
    List<TourPackage>findByCategoryId (Long categoryId);

    List<TourPackage>findByStatus (TourStatus status);

    List<TourPackage> findByPriceLessThanEqual (BigDecimal price);
}
