package com.mugen.backend.service;

import com.mugen.backend.entity.Role;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public List<User> findAll() {
        log.debug("Finding all users");
        return userRepository.findAll();
    }

    /**
     * Buscar todos os players do sistema
     * Usado em: AdminController - GET /api/v1/admin/players
     *
     * @return Lista com todos os usuários (players)
     */
    @Transactional(readOnly = true)
    public List<User> findAllPlayers() {
        log.info("Fetching all players from database");
        try {
            List<User> users = userRepository.findAllPlayers();
            log.info("Successfully fetched {} players", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error fetching all players", e);
            throw e;
        }
    }

    /**
     * Buscar player por ID
     * Usado em: AdminController - GET /api/v1/admin/players/{userId}
     *
     * @param userId ID do player
     * @return User encapsulado em Optional
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID userId) {
        log.debug("Finding player by id: {}", userId);
        return userRepository.findById(userId);
    }

    public List<User> findAllActive() {
        log.debug("Finding all active users");
        return userRepository.findAllActiveUsers();
    }

//    public Optional<User> findById(UUID id) {
//        log.debug("Finding user by id: {}", id);
//        return userRepository.findById(id);
//    }

    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByEmailWithRoles(String email) {
        log.debug("Finding user by email with roles: {}", email);
        return userRepository.findByEmailWithRoles(email);
    }

    @Transactional
    public User save(User user) {
        log.info("Saving user: {}", user.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(String email, String displayName, String passwordHash) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        // Buscar role PLAYER como padrão
        Role playerRole = roleService.findByRoleName(Role.RoleName.PLAYER)
                .orElseThrow(() -> new IllegalStateException("PLAYER role not found"));

        User user = User.builder()
                .email(email)
                .displayName(displayName)
                .passwordHash(passwordHash)
                .isActive(true)
                .build();

        user.addRole(playerRole);

        return userRepository.save(user);
    }

    @Transactional
    public User promoteToMaster(UUID userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Role masterRole = roleService.findByRoleName(Role.RoleName.MASTER)
                .orElseThrow(() -> new IllegalStateException("MASTER role not found"));

        user.addRole(masterRole);
        return userRepository.save(user);
    }

    @Transactional
    public User deactivateUser(UUID userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setIsActive(false);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Deleting user: {}", userId);
        userRepository.deleteById(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
