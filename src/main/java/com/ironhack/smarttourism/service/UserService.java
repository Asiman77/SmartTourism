package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.ChangePasswordRequest;
import com.ironhack.smarttourism.dto.request.UserRequestDTO;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAllByRole(RoleName.USER);
    }

    public User getUserById(Long id) {
        User user = findOnlyUser(id);
        return user;
    }

    public void deleteUser(Long id) {
        User user = findOnlyUser(id);
        userRepository.delete(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateProfile(String email, UserRequestDTO request) {
        User user = getCurrentUser(email);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return userRepository.save(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getCurrentUser(email);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    private User findOnlyUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != RoleName.USER) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

}