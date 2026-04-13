package com.ironhack.smarttourism.repositories;

import com.ironhack.smarttourism.entity.Destination;
import com.ironhack.smarttourism.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination,Long> {
    List<Destination> findBySeason (Season season);
    List<Destination>findByCountry (String country);
}
