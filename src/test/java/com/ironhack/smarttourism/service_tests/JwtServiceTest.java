package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
        );

        userDetails = User.builder()
                .username("user@test.com")
                .password("password")
                .authorities("USER")
                .build();
    }

    @Test
    void generateToken_ShouldCreateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_ShouldReturnUsername() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("user@test.com", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        String token = jwtService.generateToken(userDetails);

        boolean result = jwtService.isTokenValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = User.builder()
                .username("another@test.com")
                .password("password")
                .authorities("USER")
                .build();

        boolean result = jwtService.isTokenValid(token, anotherUser);

        assertFalse(result);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldCreateTokenWithClaims() {
        String token = jwtService.generateToken(
                Map.of("role", "ADMIN"),
                userDetails
        );

        String role = jwtService.extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );

        assertEquals("ADMIN", role);
    }
}