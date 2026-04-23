package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;  // ← dəyişdi

    @Transactional
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Yanlış və ya köhnəlmiş token"));

        if (user.isEnabled()) {
            throw new RuntimeException("Email artıq doğrulanmışdır");
        }

        user.setEnabled(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        if (user.isEnabled()) {
            throw new RuntimeException("Email artıq doğrulanmışdır");
        }

        sendVerificationEmail(user);
    }
}