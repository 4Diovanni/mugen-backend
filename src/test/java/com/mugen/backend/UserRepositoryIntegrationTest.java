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

    private Role playerRole;

    @BeforeEach
    void setUp() {
        // Limpar dados
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Criar role
        playerRole = Role.builder()
                .name("PLAYER")
                .description("Player role")
                .build();
        playerRole = roleRepository.save(playerRole);
    }

    // ✅ HELPER: Criar User válido
    private User createValidUser(String email, String displayName) {
        Set<Role> roles = new HashSet<>();
        roles.add(playerRole);

        return User.builder()
                .email(email)
                .displayName(displayName)
                .passwordHash("hashed_password_123")
                .isActive(true)
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        // Given
        User testUser = createValidUser("test@mugenrpg.com", "Test User");

        // When
        User saved = userRepository.save(testUser);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@mugenrpg.com");
        assertThat(saved.getDisplayName()).isEqualTo("Test User");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed_password_123");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getRoles()).hasSize(1);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        User testUser = createValidUser("test@mugenrpg.com", "Test User");
        userRepository.save(testUser);

        // When
        Optional<User> found = userRepository.findByEmail("test@mugenrpg.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@mugenrpg.com");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@email.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        User testUser = createValidUser("test@mugenrpg.com", "Test User");
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@mugenrpg.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@email.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should load user with roles using fetch join")
    void shouldLoadUserWithRoles() {
        // Given
        User testUser = createValidUser("test@mugenrpg.com", "Test User");
        userRepository.save(testUser);

        // When
        Optional<User> found = userRepository.findByEmailWithRoles("test@mugenrpg.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRoles()).isNotEmpty();
        assertThat(found.get().getRoles()).hasSize(1);
        assertThat(found.get().getRoles().iterator().next().getName()).isEqualTo("PLAYER");
    }
}
