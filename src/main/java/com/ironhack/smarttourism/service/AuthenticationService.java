package com.ironhack.smarttourism.service;

import com.ironhack.smarttourism.dto.request.AuthRequest;
import com.ironhack.smarttourism.dto.request.RegisterRequest;
import com.ironhack.smarttourism.dto.response.AuthResponse;
import com.ironhack.smarttourism.entity.Agency;
import com.ironhack.smarttourism.entity.Token;
import com.ironhack.smarttourism.entity.User;
import com.ironhack.smarttourism.entity.enums.AgencyStatus;
import com.ironhack.smarttourism.entity.enums.RoleName;
import com.ironhack.smarttourism.entity.enums.TokenType; // Enum-un doğru yerdə olduğundan əmin ol
import com.ironhack.smarttourism.repository.AgencyRepository;
import com.ironhack.smarttourism.repository.TokenRepository;
import com.ironhack.smarttourism.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final AgencyRepository agencyRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        var savedUser = userRepository.save(user);

        if (request.role() == RoleName.AGENCY) {

            if (request.agencyName() == null || request.agencyAddress() == null) {
                throw new RuntimeException("Agency məlumatları tam deyil");
            }

            Agency agency = new Agency();
            agency.setUser(savedUser);
            agency.setStatus(AgencyStatus.PENDING);

            agency.setName(request.agencyName());
            agency.setEmail(savedUser.getEmail());
            agency.setAddress(request.agencyAddress());
            agency.setPhone(request.agencyPhone());
            agency.setDescription(request.description());

            agencyRepository.save(agency);
        }

        var jwtToken = jwtService.generateToken(savedUser);
        saveUserToken(savedUser, jwtToken);

        return new AuthResponse(jwtToken);
    }

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        if (user.getRole() == RoleName.AGENCY) {
            Agency agency = agencyRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Agency tapılmadı"));

            if (agency.getStatus() != AgencyStatus.APPROVED) {
                throw new RuntimeException("Agency təsdiqlənməyib");
            }
        }

        var jwtToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return new AuthResponse(jwtToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}