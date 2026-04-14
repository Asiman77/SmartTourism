package com.ironhack.smarttourism.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 1. Hamı üçün açıqdır (Hətta login olmayanlar üçün)
    @GetMapping("/all")
    public String publicEndpoint() {
        return "Bu endpoint hamı üçün açıqdır.";
    }

    // 2. Sadece ADMIN daxil ola bilər
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Xoş gəldin Admin! Sən bütün sistemi idarə edə bilərsən.";
    }

    // 3. Sadece AGENCY daxil ola bilər
    @GetMapping("/agency")
    @PreAuthorize("hasRole('AGENCY')")
    public String agencyOnly() {
        return "Xoş gəldin Agentlik! Sən öz turlarını və rezervasiyalarını görürsən.";
    }

    // 4. Sadece USER (Sıravi müştəri) daxil ola bilər
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userOnly() {
        return "Xoş gəldin İstifadəçi! Sən turlara baxa və sifariş verə bilərsən.";
    }

    // 5. Həm ADMIN, həm də AGENCY daxil ola bilər
    @GetMapping("/shared")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENCY')")
    public String sharedEndpoint() {
        return "Bu mesajı həm Adminlər, həm də Agentliklər görə bilər.";
    }
}
