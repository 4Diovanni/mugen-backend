package com.mugen.backend;

import com.mugen.backend.entity.Role;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.UserRepository;
import com.mugen.backend.service.RoleService;
import com.mugen.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;
    private Role playerRole;
    private Role masterRole;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .email("test@mugen.com")
                .displayName("TestUser")
                .passwordHash("hashed_password")
                .isActive(true)
                .build();

        playerRole = Role.builder().id(1).name(Role.RoleName.PLAYER.name()).build();
        masterRole = Role.builder().id(2).name(Role.RoleName.MASTER.name()).build();
    }

    @Test
    void findAllPlayers_shouldReturnListOfUsers() {
        List<User> expectedUsers = Arrays.asList(testUser, new User());
        when(userRepository.findAllPlayers()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.findAllPlayers();

        assertEquals(expectedUsers.size(), actualUsers.size());
        verify(userRepository, times(1)).findAllPlayers();
    }

    @Test
    void findById_shouldReturnUser_whenFound() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(testUserId);

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnUser_whenFound() {
        when(userRepository.findByEmail("test@mugen.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@mugen.com");

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void createUser_shouldCreateAndSaveNewUser_withPlayerRole() {
        String email = "new@mugen.com";
        String displayName = "NewUser";
        String passwordHash = "new_hash";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleService.findByRoleName(Role.RoleName.PLAYER)).thenReturn(Optional.of(playerRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID()); // Simula o ID gerado pelo banco
            return user;
        });

        User createdUser = userService.createUser(email, displayName, passwordHash);

        assertNotNull(createdUser);
        assertEquals(email, createdUser.getEmail());
        assertTrue(createdUser.getRoles().contains(playerRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenUserAlreadyExists() {
        String email = "existing@mugen.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(email, "Display", "hash"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenPlayerRoleNotFound() {
        String email = "new@mugen.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleService.findByRoleName(Role.RoleName.PLAYER)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                userService.createUser(email, "Display", "hash"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void promoteToMaster_shouldAddMasterRoleAndSave() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(roleService.findByRoleName(Role.RoleName.MASTER)).thenReturn(Optional.of(masterRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User promotedUser = userService.promoteToMaster(testUserId);

        assertTrue(promotedUser.getRoles().contains(masterRole));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void promoteToMaster_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userService.promoteToMaster(testUserId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deactivateUser_shouldSetIsActiveToFalseAndSave() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User deactivatedUser = userService.deactivateUser(testUserId);

        assertFalse(deactivatedUser.getIsActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void deleteUser_shouldCallRepositoryDeleteById() {
        userService.deleteUser(testUserId);

        verify(userRepository, times(1)).deleteById(testUserId);
    }
}
