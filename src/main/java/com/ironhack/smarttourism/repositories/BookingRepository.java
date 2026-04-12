package com.ironhack.smarttourism.repositories;

import com.ironhack.smarttourism.entity.Booking;
import com.ironhack.smarttourism.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking>findByTourPackageAgencyId (Long agencyId);

    List<Booking>findByTourPackageIdAndStatus(Long tourPackageId, BookingStatus status);
}
