package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.AuthenticationController;
import com.ironhack.smarttourism.dto.request.AuthRequest;
import com.ironhack.smarttourism.dto.request.RegisterRequest;
import com.ironhack.smarttourism.dto.response.AuthResponse;
import com.ironhack.smarttourism.service.AuthenticationService;
import com.ironhack.smarttourism.service.EmailVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void register_ShouldReturnOk() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "password123",
                com.ironhack.smarttourism.entity.enums.RoleName.AGENCY,
                "Baku Tours",
                "Nizami str. 10",
                "+994501112233",
                "Professional travel agency"
        );

        // AuthResponse bir record-dur, tokeni konstruktorda veririk:
        AuthResponse response = new AuthResponse("mock-jwt-token");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void authenticate_ShouldReturnOk() throws Exception {
        // AuthRequest bir record-dur, email və password-u konstruktorda veririk:
        AuthRequest request = new AuthRequest("test@mail.com", "password");

        // AuthResponse record-dur:
        AuthResponse response = new AuthResponse("login-token");

        when(authenticationService.authenticate(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void verifyEmail_ShouldReturnSuccessMessage() throws Exception {
        String token = "valid-token";

        // void metod olduğu üçün doNothing istifadə edirik
        doNothing().when(emailVerificationService).verifyEmail(token);

        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully!"));
    }

    @Test
    void resendVerification_ShouldReturnSuccessMessage() throws Exception {
        String email = "user@test.com";

        doNothing().when(emailVerificationService).resendVerificationEmail(email);

        mockMvc.perform(post("/api/auth/resend-verification")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification email was resent"));
    }
}
