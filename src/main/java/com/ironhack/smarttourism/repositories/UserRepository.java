package com.ironhack.smarttourism.repositories;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Long> {
    //name yox email cunki ad eyni ola biler,optionali da birden maili sehv yazma ehtimalina gore yazdim,
    //service yazanda throwexception eleyib "wrong email" filan yazmaq olar
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole (RoleName role);
}
