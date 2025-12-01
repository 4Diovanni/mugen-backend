package com.mugen.backend;

import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.CharacterAttribute;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.service.CharacterService;
import com.mugen.backend.service.RaceService;
import com.mugen.backend.service.UserService;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CharacterService Unit Tests")
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private UserService userService;

    @Mock
    private RaceService raceService;

    @InjectMocks
    private CharacterService characterService;

    private User testUser;
    private Race testRace;
    private Character testCharacter;
    private UUID userId;
    private UUID characterId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        characterId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .displayName("Test User")
                .isActive(true)
                .build();

        testRace = Race.builder()
                .id(1)
                .name("Saiyan")
                .startStr(12)
                .startDex(10)
                .startCon(10)
                .startWil(14)
                .startMnd(8)
                .startSpi(10)
                .isActive(true)
                .build();

        CharacterAttribute attributes = CharacterAttribute.builder()
                .str(12)
                .dex(10)
                .con(10)
                .wil(14)
                .mnd(8)
                .spi(10)
                .build();

        testCharacter = Character.builder()
                .id(characterId)
                .owner(testUser)
                .name("Goku")
                .race(testRace)
                .level(1)
                .exp(0L)
                .tp(10)
                .isActive(true)
                .attributes(attributes)
                .build();

        attributes.setCharacter(testCharacter);
    }

    @Test
    @DisplayName("Should find all characters")
    void shouldFindAllCharacters() {
        // Given
        Character character2 = Character.builder()
                .id(UUID.randomUUID())
                .owner(testUser)
                .name("Vegeta")
                .race(testRace)
                .build();

        List<Character> characters = Arrays.asList(testCharacter, character2);
        when(characterRepository.findAll()).thenReturn(characters);

        // When
        List<Character> result = characterService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testCharacter, character2);
        verify(characterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find characters by owner id")
    void shouldFindCharactersByOwnerId() {
        // Given
        List<Character> characters = Arrays.asList(testCharacter);
        when(characterRepository.findByOwnerId(userId)).thenReturn(characters);

        // When
        List<Character> result = characterService.findByOwnerId(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Goku");
        verify(characterRepository, times(1)).findByOwnerId(userId);
    }

    @Test
    @DisplayName("Should find character by id")
    void shouldFindCharacterById() {
        // Given
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        // When
        Optional<Character> result = characterService.findById(characterId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Goku");
        verify(characterRepository, times(1)).findById(characterId);
    }

    @Test
    @DisplayName("Should create character successfully")
    void shouldCreateCharacter() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(0L);
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));
        when(raceService.findById(1)).thenReturn(Optional.of(testRace));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        // When
        Character result = characterService.createCharacter(userId, "Goku", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Goku");
        assertThat(result.getRace().getName()).isEqualTo("Saiyan");
        assertThat(result.getLevel()).isEqualTo(1);
        assertThat(result.getTp()).isEqualTo(10);
        verify(characterRepository, times(1)).countByOwnerId(userId);
        verify(userService, times(1)).findById(userId);
        verify(raceService, times(1)).findById(1);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when creating character with duplicate name")
    void shouldThrowExceptionWhenCreatingCharacterWithDuplicateName() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(0L);
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(userId, "Goku", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Character name already exists");

        verify(characterRepository, times(1)).countByOwnerId(userId);
        verify(characterRepository, times(1)).existsByOwnerIdAndName(userId, "Goku");
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when user reaches character limit")
    void shouldThrowExceptionWhenUserReachesCharacterLimit() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(userId, "NewChar", 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("maximum character limit");

        verify(characterRepository, times(1)).countByOwnerId(userId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(0L);
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userService.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(userId, "Goku", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userService, times(1)).findById(userId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when race not found")
    void shouldThrowExceptionWhenRaceNotFound() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(0L);
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));
        when(raceService.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(userId, "Goku", 999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Race not found");

        verify(raceService, times(1)).findById(999);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when race is not active")
    void shouldThrowExceptionWhenRaceIsNotActive() {
        // Given
        testRace.setIsActive(false);
        when(characterRepository.countByOwnerId(userId)).thenReturn(0L);
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userService.findById(userId)).thenReturn(Optional.of(testUser));
        when(raceService.findById(1)).thenReturn(Optional.of(testRace));

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(userId, "Goku", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Race is not active");

        verify(raceService, times(1)).findById(1);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should update character")
    void shouldUpdateCharacter() {
        // Given
        testCharacter.setLevel(5);
        testCharacter.setExp(1000L);
        when(characterRepository.save(testCharacter)).thenReturn(testCharacter);

        // When
        Character result = characterService.updateCharacter(testCharacter);

        // Then
        assertThat(result.getLevel()).isEqualTo(5);
        assertThat(result.getExp()).isEqualTo(1000L);
        verify(characterRepository, times(1)).save(testCharacter);
    }

    @Test
    @DisplayName("Should delete character")
    void shouldDeleteCharacter() {
        // Given
        doNothing().when(characterRepository).deleteById(characterId);

        // When
        characterService.deleteCharacter(characterId);

        // Then
        verify(characterRepository, times(1)).deleteById(characterId);
    }

    @Test
    @DisplayName("Should check if user is owner")
    void shouldCheckIfUserIsOwner() {
        // Given
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        // When
        boolean result = characterService.isOwner(characterId, userId);

        // Then
        assertThat(result).isTrue();
        verify(characterRepository, times(1)).findById(characterId);
    }

    @Test
    @DisplayName("Should return false when user is not owner")
    void shouldReturnFalseWhenUserIsNotOwner() {
        // Given
        UUID differentUserId = UUID.randomUUID();
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        // When
        boolean result = characterService.isOwner(characterId, differentUserId);

        // Then
        assertThat(result).isFalse();
        verify(characterRepository, times(1)).findById(characterId);
    }

    @Test
    @DisplayName("Should return false when character not found")
    void shouldReturnFalseWhenCharacterNotFound() {
        // Given
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        // When
        boolean result = characterService.isOwner(characterId, userId);

        // Then
        assertThat(result).isFalse();
        verify(characterRepository, times(1)).findById(characterId);
    }

    @Test
    @DisplayName("Should count characters by owner id")
    void shouldCountCharactersByOwnerId() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(3L);

        // When
        long result = characterService.countByOwnerId(userId);

        // Then
        assertThat(result).isEqualTo(3L);
        verify(characterRepository, times(1)).countByOwnerId(userId);
    }
}
