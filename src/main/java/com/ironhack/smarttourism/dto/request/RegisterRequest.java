package com.ironhack.smarttourism.dto.request;

import com.ironhack.smarttourism.entity.enums.RoleName;

public record RegisterRequest(String fullName, String email, String password, RoleName role,         String agencyName,
                              String agencyAddress,
                              String agencyPhone,
                              String description) {}
