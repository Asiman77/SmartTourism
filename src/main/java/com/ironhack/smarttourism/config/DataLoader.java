package com.ironhack.smarttourism.config;

import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = "admin@smarttourism.com";

        if (userRepository.existsByEmail(adminEmail)) {
            return; // artıq varsa heç nə etmir
        }

        User admin = new User();
        admin.setFullName("System Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123")); // şifrə
        admin.setEnabled(true);
        admin.setPhone("0500000000");
        admin.setRole(RoleName.ADMIN);
        userRepository.save(admin);

        System.out.println("✅ Admin user created: admin@smarttourism.com / admin123");
    }
}