package com.ironhack.smarttourism.repository;

import com.ironhack.smarttourism.entity.TourPackage;
import com.ironhack.smarttourism.entity.enums.TourStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TourPackageRepository extends JpaRepository <TourPackage,Long> {
    List<TourPackage> findByAgencyId (Long agencyId);
    List<TourPackage>findByDestinationId (Long destinationId);
    List<TourPackage>findByCategoryId (Long categoryId);

    List<TourPackage>findByStatus (TourStatus status);

    List<TourPackage> findByPriceLessThanEqual (BigDecimal price);

    @Query("""
        SELECT t FROM TourPackage t
        WHERE (:destinationId IS NULL OR t.destination.id = :destinationId)
        AND (:categoryId IS NULL OR t.category.id = :categoryId)
        AND (:minPrice IS NULL OR t.price >= :minPrice)
        AND (:maxPrice IS NULL OR t.price <= :maxPrice)
        AND (:status IS NULL OR t.status = :status)
        AND (
            :keyword IS NULL OR
            LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<TourPackage> filterTours(
            @Param("destinationId") Long destinationId,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") TourStatus status,
            @Param("keyword") String keyword
    );
}