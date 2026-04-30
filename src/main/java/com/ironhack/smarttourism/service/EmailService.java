package com.ironhack.smarttourism.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String toName, String token) {
        String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;

        String html = """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;">
                    <h2 style="color: #4CAF50;">Email Verification</h2>
                
                    <p>Hello, <strong>%s</strong>!</p>
                
                    <p>Thank you for registering on the Smart Tourism platform.</p>
                
                    <p>Please click the button below to verify your email address:</p>
                
                    <a href="%s"
                       style="background-color:#4CAF50; color:white; padding:14px 24px;
                              text-decoration:none; border-radius:4px; display:inline-block; margin: 20px 0;">
                        ✅ Verify Email
                    </a>
                
                    <p style="color:#999; font-size:12px;">
                        This link is valid for 24 hours.<br>
                        If you did not register, please ignore this email.
                    </p>
                </body>
                </html>
                """.formatted(toName, verificationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Email Verification - Smart Tourism");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Email couldn't sent: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String toEmail, String toName) {
        String html = """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;">
                    <h2>🎉 Xoş gəldiniz, %s!</h2>
                    <p>Email ünvanınız uğurla doğrulandı.</p>
                    <p>Smart Tourism platformundan istifadə edə bilərsiniz.</p>
                </body>
                </html>
                """.formatted(toName);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome! - Smart Tourism");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }
}