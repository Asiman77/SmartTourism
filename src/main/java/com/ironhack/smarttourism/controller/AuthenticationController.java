package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.dto.request.AuthRequest;
import com.ironhack.smarttourism.dto.request.RegisterRequest;
import com.ironhack.smarttourism.dto.response.AuthResponse;
import com.ironhack.smarttourism.service.AuthenticationService;
import com.ironhack.smarttourism.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    // Email doğrulama
    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email uğurla doğrulandı!"));
    }

    // Yenidən göndər
    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestParam String email) {
        emailVerificationService.resendVerificationEmail(email);
        return ResponseEntity.ok(Map.of("message", "Doğrulama emaili yenidən göndərildi"));
    }
}