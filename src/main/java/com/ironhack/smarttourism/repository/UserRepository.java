package com.ironhack.smarttourism.repository;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Long> {
    //we used email because name could be the same.Optional is added in case of wrong email input
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    List<User> findAllByRole(RoleName role);

    Optional<User> findByEmailVerificationToken(String token);
}