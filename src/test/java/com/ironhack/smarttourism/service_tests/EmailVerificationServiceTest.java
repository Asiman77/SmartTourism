package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.repository.UserRepository;
import com.ironhack.smarttourism.service.EmailService;
import com.ironhack.smarttourism.service.EmailVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("user@test.com");
        sampleUser.setFullName("Test User");
        sampleUser.setEnabled(false);
    }

    @Test
    void sendVerificationEmail_ShouldGenerateToken_AndSendEmail() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        emailVerificationService.sendVerificationEmail(sampleUser);

        assertNotNull(sampleUser.getEmailVerificationToken());
        verify(userRepository).save(sampleUser);
        verify(emailService).sendVerificationEmail(
                eq(sampleUser.getEmail()),
                eq(sampleUser.getFullName()),
                anyString()
        );
    }

    @Test
    void verifyEmail_ShouldEnableUser_AndSendWelcomeEmail() {
        sampleUser.setEmailVerificationToken("token123");

        when(userRepository.findByEmailVerificationToken("token123"))
                .thenReturn(Optional.of(sampleUser));

        emailVerificationService.verifyEmail("token123");

        assertTrue(sampleUser.isEnabled());
        assertNull(sampleUser.getEmailVerificationToken());

        verify(userRepository).save(sampleUser);
        verify(emailService).sendWelcomeEmail(
                sampleUser.getEmail(),
                sampleUser.getFullName()
        );
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenTokenInvalid() {
        when(userRepository.findByEmailVerificationToken("bad-token"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.verifyEmail("bad-token")
        );

        assertEquals("Wrong or Old Token", exception.getMessage());
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenAlreadyEnabled() {
        sampleUser.setEnabled(true);
        sampleUser.setEmailVerificationToken("token123");

        when(userRepository.findByEmailVerificationToken("token123"))
                .thenReturn(Optional.of(sampleUser));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.verifyEmail("token123")
        );

        assertEquals("Email has already been verified", exception.getMessage());
    }

    @Test
    void resendVerificationEmail_ShouldCallSendVerificationEmail() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(sampleUser));

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        emailVerificationService.resendVerificationEmail("user@test.com");

        verify(emailService).sendVerificationEmail(
                eq(sampleUser.getEmail()),
                eq(sampleUser.getFullName()),
                anyString()
        );
    }

    @Test
    void resendVerificationEmail_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.resendVerificationEmail("user@test.com")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void resendVerificationEmail_ShouldThrowException_WhenAlreadyVerified() {
        sampleUser.setEnabled(true);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(sampleUser));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailVerificationService.resendVerificationEmail("user@test.com")
        );

        assertEquals("Email has already been verified", exception.getMessage());
    }
}