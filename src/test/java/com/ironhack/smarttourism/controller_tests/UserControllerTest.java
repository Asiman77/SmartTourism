package com.ironhack.smarttourism.controller_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.smarttourism.controller.UserController;
import com.ironhack.smarttourism.dto.request.ChangePasswordRequest;
import com.ironhack.smarttourism.dto.request.UserRequestDTO;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Controller-in "Authentication auth" hissəsini aldatmaq üçün bu lazımdır
    private final UsernamePasswordAuthenticationToken mockAuth =
            new UsernamePasswordAuthenticationToken("test@example.com", null);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getMe_ShouldReturnUserProfile() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(RoleName.USER);

        when(userService.getCurrentUser(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/users/me")
                        .principal(mockAuth)) // Budur həll yolu
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void updateMe_ShouldReturnUpdatedProfile() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setFullName("New Name");
        request.setEmail("test@example.com");
        request.setRole(RoleName.USER);

        User updated = new User();
        updated.setFullName("New Name");
        updated.setEmail("test@example.com");

        when(userService.updateProfile(anyString(), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/users/me")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("New Name"));
    }

    @Test
    void changePassword_ShouldReturnSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123");
        request.setNewPassword("456");

        mockMvc.perform(patch("/api/users/change-password")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed"));
    }
}