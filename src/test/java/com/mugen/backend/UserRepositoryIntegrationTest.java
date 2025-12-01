package com.mugen.backend;

import com.mugen.backend.entity.Role;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.RoleRepository;
import com.mugen.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role playerRole;

    @BeforeEach
    void setUp() {
        System.out.println("========== SETUP STARTED ==========");

        // Limpar dados
        userRepository.deleteAll();
        roleRepository.deleteAll();

        System.out.println("DEBUG - Tables cleaned");

        // Criar role
        playerRole = Role.builder()
                .name("PLAYER")
                .description("Player role")
                .build();

        System.out.println("DEBUG - Role created (before save): " + playerRole);
        System.out.println("DEBUG - Role ID (before save): " + playerRole.getId());

        playerRole = roleRepository.saveAndFlush(playerRole);

        System.out.println("DEBUG - Role saved!");
        System.out.println("DEBUG - PlayerRole ID (after save): " + playerRole.getId());
        System.out.println("DEBUG - PlayerRole Name: " + playerRole.getName());

        // Criar usuário
        testUser = User.builder()
                .email("test@example.com")
                .displayName("Test User")
                .passwordHash("hashed_password")
                .isActive(true)
                .roles(new HashSet<>(Set.of(playerRole)))
                .build();

        System.out.println("DEBUG - TestUser created (before save)");
        System.out.println("DEBUG - TestUser roles: " + testUser.getRoles().size());

        testUser = userRepository.saveAndFlush(testUser);

        System.out.println("DEBUG - TestUser saved!");
        System.out.println("DEBUG - TestUser ID: " + testUser.getId());
        System.out.println("DEBUG - TestUser roles after save: " + testUser.getRoles().size());
        System.out.println("========== SETUP COMPLETED ==========");
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        System.out.println("========== TEST STARTED ==========");
        System.out.println("DEBUG - PlayerRole in test: " + playerRole);
        System.out.println("DEBUG - PlayerRole ID in test: " + (playerRole != null ? playerRole.getId() : "NULL"));

        // Given
        User newUser = User.builder()
                .email("new@example.com")
                .displayName("New User")
                .passwordHash("hashed")
                .isActive(true)
                .roles(new HashSet<>(Set.of(playerRole)))
                .build();

        System.out.println("DEBUG - NewUser created");
        System.out.println("DEBUG - NewUser roles before save: " + newUser.getRoles().size());
        System.out.println("DEBUG - PlayerRole ID used: " + playerRole.getId());

        // When
        User saved = userRepository.saveAndFlush(newUser);

        System.out.println("DEBUG - User saved!");
        System.out.println("DEBUG - Saved user ID: " + saved.getId());
        System.out.println("DEBUG - Saved user email: " + saved.getEmail());
        System.out.println("DEBUG - Saved user roles: " + (saved.getRoles() != null ? saved.getRoles().size() : "NULL"));

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getRoles()).isNotNull();  // ← Linha 79 (provavelmente esta)
        assertThat(saved.getRoles()).hasSize(1);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        System.out.println("========== TEST COMPLETED ==========");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDisplayName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notfound@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should find all active users")
    void shouldFindAllActiveUsers() {
        // Given
        User inactiveUser = User.builder()
                .email("inactive@example.com")
                .displayName("Inactive")
                .isActive(false)
                .build();
        userRepository.save(inactiveUser);

        // When
        List<User> activeUsers = userRepository.findAllActiveUsers();

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find user by email with roles")
    void shouldFindUserByEmailWithRoles() {
        // When
        Optional<User> found = userRepository.findByEmailWithRoles("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRoles()).isNotEmpty();
        assertThat(found.get().getRoles()).extracting("name").contains("PLAYER");
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        // Given
        testUser.setDisplayName("Updated Name");

        // When
        userRepository.saveAndFlush(testUser);
        User updated = userRepository.findById(testUser.getId()).orElseThrow();  // ✅ Recarrega do banco

        // Then
        assertThat(updated.getDisplayName()).isEqualTo("Updated Name");
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }


    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // When
        userRepository.delete(testUser);

        // Then
        assertThat(userRepository.findByEmail("test@example.com")).isEmpty();
    }
}
