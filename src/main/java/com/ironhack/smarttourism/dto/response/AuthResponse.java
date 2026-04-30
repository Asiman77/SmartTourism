package com.ironhack.smarttourism.dto.response;

import com.ironhack.smarttourism.entity.enums.RoleName;

public record AuthResponse(String token, RoleName role) {}
