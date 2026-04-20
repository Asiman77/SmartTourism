package com.ironhack.smarttourism.repository;

import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository <Agency,Long> {
    List<Agency> findByStatus (AgencyStatus status);

    Optional<Agency> findByUserId(Long userId);
    Optional<Agency> findByUser(User user);

    boolean existsByName(String name);
    boolean existsByEmail (String email);
}