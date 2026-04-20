package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.request.UserRequestDTO;
import com.ironhack.smarttourism.dto.response.UserResponseDTO;
import com.ironhack.smarttourism.entity.User;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public static User toUser(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        return user;
    }
}
