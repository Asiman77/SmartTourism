package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.service.EmailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));

        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@smarttourism.com");
        ReflectionTestUtils.setField(emailService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void sendVerificationEmail_ShouldSendEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail(
                "user@test.com",
                "Test User",
                "abc-token"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendVerificationEmail_ShouldThrowException_WhenEmailFails() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(mimeMessage);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService.sendVerificationEmail(
                        "user@test.com",
                        "Test User",
                        "abc-token"
                )
        );

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendWelcomeEmail_ShouldSendEmail() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendWelcomeEmail(
                "user@test.com",
                "Test User"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendWelcomeEmail_ShouldNotThrowException_WhenEmailFails() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(mimeMessage);

        emailService.sendWelcomeEmail(
                "user@test.com",
                "Test User"
        );

        verify(mailSender).send(mimeMessage);
    }
}