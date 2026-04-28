package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.ChangePasswordRequest;
import com.ironhack.smarttourism.dto.request.UserRequestDTO;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.exception.ResourceNotFoundException;
import com.ironhack.smarttourism.repository.UserRepository;
import com.ironhack.smarttourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private final Long userId = 1L;
    private final String email = "user@test.com";

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(userId);
        sampleUser.setEmail(email);
        sampleUser.setPassword("encodedPassword");
        sampleUser.setRole(RoleName.USER);
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAllByRole(RoleName.USER))
                .thenReturn(List.of(sampleUser));

        List<User> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(userRepository).findAllByRole(RoleName.USER);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(sampleUser));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(sampleUser));

        userService.deleteUser(userId);

        verify(userRepository).delete(sampleUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(userId));
    }

    @Test
    void getCurrentUser_ShouldReturnUser() {
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(sampleUser));

        User result = userService.getCurrentUser(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getCurrentUser(email));
    }

    @Test
    void updateProfile_ShouldUpdateFields() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFullName("New Name");
        dto.setPhone("+994501112233");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateProfile(email, dto);

        assertEquals("New Name", updated.getFullName());
        assertEquals("+994501112233", updated.getPhone());

        verify(userRepository).save(sampleUser);
    }

    @Test
    void changePassword_ShouldChangePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword"))
                .thenReturn(true);
        when(passwordEncoder.encode("newPass"))
                .thenReturn("newEncoded");

        userService.changePassword(email, request);

        assertEquals("newEncoded", sampleUser.getPassword());

        verify(userRepository).save(sampleUser);
    }

    @Test
    void changePassword_ShouldThrowException_WhenWrongPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPass");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong", "encodedPassword"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.changePassword(email, request)
        );

        assertEquals("Wrong password", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}
