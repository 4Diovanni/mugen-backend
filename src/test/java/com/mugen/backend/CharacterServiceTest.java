package com.mugen.backend;

import com.mugen.backend.dto.character.CharacterDTO;
import com.mugen.backend.dto.character.UpdateCharacterDTO;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.Skill;
import com.mugen.backend.entity.User;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterSkill;
import com.mugen.backend.entity.character.CharacterSkillId;
import com.mugen.backend.exception.CharacterNotFoundException;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.RaceRepository;
import com.mugen.backend.repository.UserRepository;
import com.mugen.backend.repository.skills.CharacterSkillRepository;
import com.mugen.backend.repository.skills.SkillRepository;
import com.mugen.backend.service.CharacterService;
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
class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RaceRepository raceRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private CharacterSkillRepository characterSkillRepository;

    @InjectMocks
    private CharacterService characterService;

    private UUID characterId;
    private UUID ownerId;
    private Character testCharacter;
    private User testUser;
    private Race testRace;

    @BeforeEach
    void setUp() {
        characterId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        testUser = User.builder().id(ownerId).email("owner@test.com").build();
        testRace = Race.builder().id(1).name("Human").build();
        testCharacter = Character.builder()
                .id(characterId)
                .name("Goku")
                .owner(testUser)
                .race(testRace)
                .level(1)
                .exp(0L)
                .tp(0)
                .isActive(true)
                .build();
    }

    // ========== Testes de Criação ==========

    @Test
    void createCharacter_shouldCreateAndSaveNewCharacter() {
        CharacterDTO dto = CharacterDTO.builder().name("Vegeta").ownerId(ownerId).raceId(1).build();

        when(characterRepository.existsByOwnerIdAndName(ownerId, "Vegeta")).thenReturn(false);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(testUser));
        when(raceRepository.findById(1)).thenReturn(Optional.of(testRace));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character created = characterService.createCharacter(dto);

        assertNotNull(created);
        assertEquals("Goku", created.getName()); // Mocked save returns testCharacter
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void createCharacter_shouldThrowException_whenNameAlreadyExists() {
        CharacterDTO dto = CharacterDTO.builder().name("Goku").ownerId(ownerId).raceId(1).build();

        when(characterRepository.existsByOwnerIdAndName(ownerId, "Goku")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> characterService.createCharacter(dto));
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void createCharacter_shouldThrowException_whenUserNotFound() {
        CharacterDTO dto = CharacterDTO.builder().name("Vegeta").ownerId(ownerId).raceId(1).build();

        when(characterRepository.existsByOwnerIdAndName(ownerId, "Vegeta")).thenReturn(false);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> characterService.createCharacter(dto));
        verify(characterRepository, never()).save(any(Character.class));
    }

    // ========== Testes de Consulta ==========

    @Test
    void getCharacterById_shouldReturnCharacter_whenFound() {
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        Character found = characterService.getCharacterById(characterId);

        assertEquals(testCharacter, found);
    }

    @Test
    void getCharacterById_shouldThrowException_whenNotFound() {
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        assertThrows(CharacterNotFoundException.class, () -> characterService.getCharacterById(characterId));
    }

    @Test
    void getCharactersByOwner_shouldReturnList_whenUserExists() {
        List<Character> expectedList = Arrays.asList(testCharacter);
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(characterRepository.findByOwnerId(ownerId)).thenReturn(expectedList);

        List<Character> actualList = characterService.getCharactersByOwner(ownerId);

        assertEquals(expectedList, actualList);
    }

    @Test
    void getCharactersByOwner_shouldThrowException_whenUserNotFound() {
        when(userRepository.existsById(ownerId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> characterService.getCharactersByOwner(ownerId));
    }

    // ========== Testes de Atualização ==========

    @Test
    void updateCharacter_shouldUpdateLevelAndExp() {
        UpdateCharacterDTO dto = UpdateCharacterDTO.builder().level(10).exp(5000L).build();
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character updated = characterService.updateCharacter(characterId, dto);

        assertEquals(10, updated.getLevel());
        assertEquals(5000L, updated.getExp());
        verify(characterRepository, times(1)).save(testCharacter);
    }

    @Test
    void updateCharacterName_shouldUpdateName() {
        String newName = "Gohan";
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.existsByOwnerIdAndName(ownerId, newName)).thenReturn(false);
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character updated = characterService.updateCharacterName(characterId, newName);

        assertEquals(newName, updated.getName());
        verify(characterRepository, times(1)).save(testCharacter);
    }

    // ========== Testes de Deleção ==========

    @Test
    void deleteCharacter_shouldCallRepositoryDeleteById() {
        when(characterRepository.existsById(characterId)).thenReturn(true);

        characterService.deleteCharacter(characterId);

        verify(characterRepository, times(1)).deleteById(characterId);
    }

    @Test
    void softDeleteCharacter_shouldSetIsActiveToFalse() {
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        characterService.softDeleteCharacter(characterId);

        assertFalse(testCharacter.getIsActive());
        verify(characterRepository, times(1)).save(testCharacter);
    }

    // ========== Testes de Skills ==========

    @Test
    void addSkillToCharacter_shouldAddSkillSuccessfully() {
        int skillId = 10;
        Skill testSkill = Skill.builder().id(skillId).name("Kamehameha").build();
        CharacterSkillId charSkillId = new CharacterSkillId(characterId, skillId);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(testSkill));
        when(characterSkillRepository.existsById(charSkillId)).thenReturn(false);
        when(characterSkillRepository.save(any(CharacterSkill.class))).thenAnswer(i -> i.getArgument(0));

        CharacterSkill result = characterService.addSkillToCharacter(characterId, skillId);

        assertNotNull(result);
        assertEquals(characterId, result.getId().getCharacterId());
        assertEquals(skillId, result.getId().getSkillId());
        verify(characterSkillRepository, times(1)).save(any(CharacterSkill.class));
    }

    @Test
    void addSkillToCharacter_shouldThrowException_whenSkillAlreadyExists() {
        int skillId = 10;
        CharacterSkillId charSkillId = new CharacterSkillId(characterId, skillId);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(Skill.builder().id(skillId).build()));
        when(characterSkillRepository.existsById(charSkillId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> characterService.addSkillToCharacter(characterId, skillId));
        verify(characterSkillRepository, never()).save(any(CharacterSkill.class));
    }

    @Test
    void removeSkillFromCharacter_shouldDeleteSkillSuccessfully() {
        int skillId = 10;
        CharacterSkillId charSkillId = new CharacterSkillId(characterId, skillId);

        when(characterSkillRepository.existsById(charSkillId)).thenReturn(true);
        doNothing().when(characterSkillRepository).deleteById(charSkillId);

        characterService.removeSkillFromCharacter(characterId, skillId);

        verify(characterSkillRepository, times(1)).deleteById(charSkillId);
    }
}
