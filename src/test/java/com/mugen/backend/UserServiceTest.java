package com.mugen.backend;

import com.mugen.backend.entity.Role;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.UserRepository;
import com.mugen.backend.service.RoleService;
import com.mugen.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role playerRole;
    private Role masterRole;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        playerRole = Role.builder()
                .id(1)
                .name("PLAYER")
                .description("Player role")
                .build();

        masterRole = Role.builder()
                .id(2)
                .name("MASTER")
                .description("Master role")
                .build();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .displayName("Test User")
                .passwordHash("hashed_password")
                .isActive(true)
                .build();

        testUser.addRole(playerRole);
    }

    @Test
    @DisplayName("Should find user by id")
    void shouldFindUserById() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDisplayName()).isEqualTo("Test User");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should create new user successfully")
    void shouldCreateUser() {
        // Given
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleService.findByRoleName(Role.RoleName.PLAYER)).thenReturn(Optional.of(playerRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        // When
        User result = userService.createUser("new@example.com", "New User", "hashed_pass");

        // Then
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getDisplayName()).isEqualTo("New User");
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getRoles()).contains(playerRole);
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(roleService, times(1)).findByRoleName(Role.RoleName.PLAYER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser("test@example.com", "Test", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists with email");

        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should promote user to master")
    void shouldPromoteUserToMaster() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(roleService.findByRoleName(Role.RoleName.MASTER)).thenReturn(Optional.of(masterRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.promoteToMaster(userId);

        // Then
        assertThat(result.getRoles()).hasSize(2);
        assertThat(result.getRoles()).contains(playerRole, masterRole);
        verify(userRepository, times(1)).findById(userId);
        verify(roleService, times(1)).findByRoleName(Role.RoleName.MASTER);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.deactivateUser(userId);

        // Then
        assertThat(result.getIsActive()).isFalse();
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        // When & Then
        assertThat(userService.existsByEmail("test@example.com")).isTrue();
        assertThat(userService.existsByEmail("notfound@example.com")).isFalse();

        verify(userRepository, times(2)).existsByEmail(anyString());
    }
}
