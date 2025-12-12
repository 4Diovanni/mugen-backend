package com.mugen.backend;

import com.mugen.backend.dto.character.CharacterDTO;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.RaceRepository;
import com.mugen.backend.repository.UserRepository;
import com.mugen.backend.service.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CharacterService Unit Tests")
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RaceRepository raceRepository;

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
        List<Character> result = characterService.getAllCharacters();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testCharacter, character2);
        verify(characterRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find characters by owner id")
    void shouldFindCharactersByOwnerId() {
        // Given
        List<Character> characters = Collections.singletonList(testCharacter);

        // ✅ ADICIONADO: Mock para validação de existência do usuário
        when(userRepository.existsById(userId)).thenReturn(true);
        when(characterRepository.findByOwnerId(userId)).thenReturn(characters);

        // When
        List<Character> result = characterService.getCharactersByOwner(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Goku");
        verify(userRepository, times(1)).existsById(userId);
        verify(characterRepository, times(1)).findByOwnerId(userId);
    }

    @Test
    @DisplayName("Should find character by id")
    void shouldFindCharacterById() {
        // Given
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        // When
        Character result = characterService.getCharacterById(characterId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Goku");
        verify(characterRepository, times(1)).findById(characterId);
    }

    @Test
    @DisplayName("Should create character successfully")
    void shouldCreateCharacter() {
        // Given
        CharacterDTO dto = CharacterDTO.builder()
                .ownerId(userId)
                .name("Goku")
                .raceId(1)
                .build();

        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(raceRepository.findById(1)).thenReturn(Optional.of(testRace));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        // When
        Character result = characterService.createCharacter(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Goku");
        assertThat(result.getRace().getName()).isEqualTo("Saiyan");
        assertThat(result.getLevel()).isEqualTo(1);
        assertThat(result.getTp()).isEqualTo(10);
        verify(userRepository, times(1)).findById(userId);
        verify(raceRepository, times(1)).findById(1);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when creating character with duplicate name")
    void shouldThrowExceptionWhenCreatingCharacterWithDuplicateName() {
        // Given
        CharacterDTO dto = CharacterDTO.builder()
                .ownerId(userId)
                .name("Goku")
                .raceId(1)
                .build();

        // ✅ CORRIGIDO: Mock completo antes da validação de nome duplicado
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a character with name");

        verify(characterRepository, times(1)).existsByOwnerIdAndName(userId, "Goku");
        verify(userRepository, never()).findById(any());  // ✅ Não deve chegar aqui
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        CharacterDTO dto = CharacterDTO.builder()
                .ownerId(userId)
                .name("Goku")
                .raceId(1)
                .build();

        // ✅ CORRIGIDO: Apenas mocks que serão usados
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(characterRepository, times(1)).existsByOwnerIdAndName(userId, "Goku");
        verify(userRepository, times(1)).findById(userId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should throw exception when race not found")
    void shouldThrowExceptionWhenRaceNotFound() {
        // Given
        CharacterDTO dto = CharacterDTO.builder()
                .ownerId(userId)
                .name("Goku")
                .raceId(999)
                .build();

        // ✅ CORRIGIDO: Apenas mocks que serão usados
        when(characterRepository.existsByOwnerIdAndName(userId, "Goku")).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(raceRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> characterService.createCharacter(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Race not found");

        verify(characterRepository, times(1)).existsByOwnerIdAndName(userId, "Goku");
        verify(userRepository, times(1)).findById(userId);
        verify(raceRepository, times(1)).findById(999);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    @DisplayName("Should count characters by owner id")
    void shouldCountCharactersByOwnerId() {
        // Given
        when(characterRepository.countByOwnerId(userId)).thenReturn(3L);

        // When
        long result = characterService.countCharactersByOwner(userId);

        // Then
        assertThat(result).isEqualTo(3L);
        verify(characterRepository, times(1)).countByOwnerId(userId);
    }

    @Test
    @DisplayName("Should check if character exists")
    void shouldCheckIfCharacterExists() {
        // Given
        when(characterRepository.existsById(characterId)).thenReturn(true);

        // When
        boolean result = characterService.characterExists(characterId);

        // Then
        assertThat(result).isTrue();
        verify(characterRepository, times(1)).existsById(characterId);
    }

    @Test
    @DisplayName("Should delete character")
    void shouldDeleteCharacter() {
        // Given
        when(characterRepository.existsById(characterId)).thenReturn(true);
        doNothing().when(characterRepository).deleteById(characterId);

        // When
        characterService.deleteCharacter(characterId);

        // Then
        verify(characterRepository, times(1)).existsById(characterId);
        verify(characterRepository, times(1)).deleteById(characterId);
    }
}
