package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.response.UserResponseDTO;
import com.ironhack.smarttourism.mapper.UserMapper;
import com.ironhack.smarttourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toUserResponseDTO)
                .toList();

        return new ApiResponse<>(true, "Users fetched", users);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return new ApiResponse<>(true, "User fetched", UserMapper.toUserResponseDTO(user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ApiResponse<>(true, "User deleted", null);
    }
}