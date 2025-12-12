package com.mugen.backend;

import com.mugen.backend.entity.Role;
import com.mugen.backend.repository.RoleRepository;
import com.mugen.backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Unit Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role playerRole;
    private Role masterRole;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("Should find all roles")
    void shouldFindAllRoles() {
        // Given
        List<Role> roles = Arrays.asList(playerRole, masterRole);
        when(roleRepository.findAll()).thenReturn(roles);

        // When
        List<Role> result = roleService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(playerRole, masterRole);
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find role by id")
    void shouldFindRoleById() {
        // Given
        when(roleRepository.findById(1)).thenReturn(Optional.of(playerRole));

        // When
        Optional<Role> result = roleService.findById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PLAYER");
        verify(roleRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should find role by name")
    void shouldFindRoleByName() {
        // Given
        when(roleRepository.findByName("PLAYER")).thenReturn(Optional.of(playerRole));

        // When
        Optional<Role> result = roleService.findByName("PLAYER");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        verify(roleRepository, times(1)).findByName("PLAYER");
    }

    @Test
    @DisplayName("Should save role successfully")
    void shouldSaveRole() {
        // Given
        Role newRole = Role.builder()
                .name("ADMIN")
                .description("ADMIN role")
                .build();

        Role savedRole = Role.builder()
                .id(3)
                .name("ADMIN")
                .description("ADMIN role")
                .build();

        when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

        // When
        Role result = roleService.save(newRole);

        // Then
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("ADMIN");
        verify(roleRepository, times(1)).save(newRole);
    }

    @Test
    @DisplayName("Should create new role")
    void shouldCreateRole() {
        // Given
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(4);
            return role;
        });

        // When
        Role result = roleService.createRole("ADMIN", "Admin role");

        // Then
        assertThat(result.getName()).isEqualTo("ADMIN");
        assertThat(result.getDescription()).isEqualTo("Admin role");
        verify(roleRepository, times(1)).existsByName("ADMIN");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate role")
    void shouldThrowExceptionWhenCreatingDuplicateRole() {
        // Given
        when(roleRepository.existsByName("PLAYER")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> roleService.createRole("PLAYER", "Duplicate"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role already exists");

        verify(roleRepository, times(1)).existsByName("PLAYER");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Should check if role exists by name")
    void shouldCheckIfRoleExistsByName() {
        // Given
        when(roleRepository.existsByName("PLAYER")).thenReturn(true);
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);

        // When & Then
        assertThat(roleService.existsByName("PLAYER")).isTrue();
        assertThat(roleService.existsByName("ADMIN")).isFalse();

        verify(roleRepository, times(2)).existsByName(anyString());
    }
}
