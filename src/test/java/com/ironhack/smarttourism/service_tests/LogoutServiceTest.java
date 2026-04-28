package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.entity.Token;
import com.ironhack.smarttourism.repository.TokenRepository;
import com.ironhack.smarttourism.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LogoutService logoutService;

    private Token token;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);

        token = new Token();
        token.setToken("valid-token");
        token.setExpired(false);
        token.setRevoked(false);
    }

    @Test
    void logout_ShouldDoNothing_WhenHeaderIsNull() {
        when(request.getHeader("Authorization")).thenReturn(null);

        logoutService.logout(request, response, null);

        verify(tokenRepository, never()).findByToken(any());
    }

    @Test
    void logout_ShouldDoNothing_WhenHeaderInvalid() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        logoutService.logout(request, response, null);

        verify(tokenRepository, never()).findByToken(any());
    }

    @Test
    void logout_ShouldRevokeToken_WhenTokenExists() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(tokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(token));

        logoutService.logout(request, response, null);

        assertTrue(token.isExpired());
        assertTrue(token.isRevoked());

        verify(tokenRepository).save(token);
    }

    @Test
    void logout_ShouldDoNothing_WhenTokenNotFound() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(tokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.empty());

        logoutService.logout(request, response, null);

        verify(tokenRepository, never()).save(any());
    }
}