package com.ironhack.smarttourism.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    //everyone can access
    @GetMapping("/all")
    public String publicEndpoint() {
        return "This endpoint is accessible for everyone";
    }

    //for admin only
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Welcome, Admin! You can control the whole system!";
    }

    //for agency only
    @GetMapping("/agency")
    @PreAuthorize("hasRole('AGENCY')")
    public String agencyOnly() {
        return "Welcome, Agency! You can see your own tours and reservations!";
    }

    //for user only
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userOnly() {
        return "Welcome User! You can look at tours,and book them!";
    }

    // both admin and agency can access
    @GetMapping("/shared")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENCY')")
    public String sharedEndpoint() {
        return "This message can be seen by both Admin and Agencies!";
    }
}
