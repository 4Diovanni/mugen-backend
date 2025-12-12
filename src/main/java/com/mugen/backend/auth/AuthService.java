package com.mugen.backend.auth;

import com.mugen.backend.entity.Role;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.UserRepository;
import com.mugen.backend.security.JwtTokenProvider;
import com.mugen.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleService roleService;

    public void register(String email, String password, String displayName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .passwordHash(passwordEncoder.encode(password))
                .isActive(true)
                .build();

        Role playerRole = roleService.findByName(String.valueOf(Role.RoleName.PLAYER))
                .orElseThrow(() -> new IllegalStateException("PLAYER role not found"));
        user.addRole(playerRole);

        userRepository.save(user);
        log.info("User registered: {}", email);
    }

    public String authenticate(String email, String password) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User account is deactivated");
        }

        String token = jwtTokenProvider.generateToken(user);
        log.info("User authenticated: {}", email);

        return token;
    }
}
