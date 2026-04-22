package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAllByRole(RoleName.USER);
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != RoleName.USER) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != RoleName.USER) {
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.delete(user);
    }
}