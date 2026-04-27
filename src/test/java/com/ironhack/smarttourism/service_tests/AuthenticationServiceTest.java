package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.AuthRequest;
import com.ironhack.smarttourism.dto.request.RegisterRequest;
import com.ironhack.smarttourism.dto.response.AuthResponse;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.TokenRepository;
import com.ironhack.smarttourism.repository.UserRepository;
import com.ironhack.smarttourism.service.AuthenticationService;
import com.ironhack.smarttourism.service.EmailVerificationService;
import com.ironhack.smarttourism.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenRepository tokenRepository;
    @Mock private AgencyRepository agencyRepository;
    @Mock private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthenticationService authService;

    private User sampleUser;
    private RegisterRequest userRequest;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@mail.com");
        sampleUser.setRole(RoleName.USER);

        userRequest = new RegisterRequest(
                "Test User",
                "test@mail.com",
                "password123",
                RoleName.USER,
                null, null, null, null
        );
    }

    @Test
    void register_UserRole_ShouldWorkSuccessfully() {
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake_token");

        AuthResponse response = authService.register(userRequest);

        assertNotNull(response.token());
        assertEquals("fake_token", response.token());
        verify(userRepository).save(any(User.class));
        verify(agencyRepository, never()).save(any());
    }

    @Test
    void register_AgencyRole_ShouldCreateAgencyProfile() {
        RegisterRequest agencyRequest = new RegisterRequest(
                "Agency Owner", "agency@test.com", "pass", RoleName.AGENCY,
                "Cool Tours", "Baku", "12345", "Desc"
        );

        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");

        authService.register(agencyRequest);

        verify(agencyRepository, times(1)).save(any(Agency.class));
    }

    @Test
    void register_AgencyRole_ShouldThrowException_WhenAgencyDataMissing() {
        RegisterRequest invalidAgencyRequest = new RegisterRequest(
                "Owner", "a@a.com", "p", RoleName.AGENCY,
                null, null, null, null
        );

        assertThrows(RuntimeException.class, () -> authService.register(invalidAgencyRequest));
    }

    @Test
    void authenticate_Successful_ForAdminOrUser() {
        AuthRequest authRequest = new AuthRequest("test@mail.com", "password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(jwtService.generateToken(any())).thenReturn("new_token");

        AuthResponse response = authService.authenticate(authRequest);

        assertEquals("new_token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_AgencyNotApproved_ShouldThrowException() {
        AuthRequest authRequest = new AuthRequest("agency@test.com", "pass");
        sampleUser.setRole(RoleName.AGENCY);

        Agency pendingAgency = new Agency();
        pendingAgency.setStatus(AgencyStatus.PENDING);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));
        when(agencyRepository.findByUser(any())).thenReturn(Optional.of(pendingAgency));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(authRequest));

        assertEquals("Agency isn't approvet yet", exception.getMessage());
    }
}
