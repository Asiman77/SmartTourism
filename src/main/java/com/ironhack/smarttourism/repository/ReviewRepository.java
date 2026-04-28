package com.ironhack.smarttourism.repository;

import com.ironhack.smarttourism.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByUserId(Long userId);

    List<Review> findByTourPackageId(Long tourPackageId);

    boolean existsByUserIdAndTourPackageId(Long userId, Long tourPackageId);
}