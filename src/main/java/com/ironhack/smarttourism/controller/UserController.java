package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.ChangePasswordRequest;
import com.ironhack.smarttourism.dto.request.UserRequestDTO;
import com.ironhack.smarttourism.dto.response.UserResponseDTO;
import com.ironhack.smarttourism.mapper.UserMapper;
import com.ironhack.smarttourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponseDTO> getMe(Authentication auth) {
        var user = userService.getCurrentUser(auth.getName());

        return new ApiResponse<>(
                true,
                "Profile fetched",
                UserMapper.toUserResponseDTO(user)
        );
    }

    @PatchMapping("/me")
    public ApiResponse<UserResponseDTO> updateMe(
            Authentication auth,
            @RequestBody UserRequestDTO request
    ) {
        var user = userService.updateProfile(auth.getName(), request);

        return new ApiResponse<>(
                true,
                "Profile updated",
                UserMapper.toUserResponseDTO(user)
        );
    }

    @PatchMapping("/change-password")
    public ApiResponse<String> changePassword(
            Authentication auth,
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(auth.getName(), request);

        return new ApiResponse<>(true, "Password changed", null);
    }
}