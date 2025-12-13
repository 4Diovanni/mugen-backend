package com.mugen.backend;

import com.mugen.backend.dto.tp.GainExpRequest;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.service.ExperienceService;
import com.mugen.backend.service.ExperienceService.LevelProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @InjectMocks
    private ExperienceService experienceService;

    private UUID characterId;
    private Character testCharacter;

    @BeforeEach
    void setUp() {
        characterId = UUID.randomUUID();
        testCharacter = Character.builder()
                .id(characterId)
                .name("Goku")
                .level(1)
                .exp(0L)
                .tp(0)
                .build();
    }

    // ========== Testes de Cálculo de XP ==========

    @Test
    void getExpRequiredForLevel_shouldReturnBaseExpForLevel1() {
        // Level 1 não existe na progressão, mas o método calcula o XP necessário para o próximo level
        // O método calcula o XP necessário para *chegar* ao level N, que é o XP do level N-1
        // O código do serviço está calculando o XP necessário para o level N+1
        // Level 2 (i=1): 100
        assertEquals(100L, experienceService.getExpRequiredForLevel(1));
    }

    @Test
    void getExpRequiredForLevel_shouldReturnCorrectExpForLevel2() {
        // Level 2: 100 * 1.1 = 110
        assertEquals(110L, experienceService.getExpRequiredForLevel(2));
    }

    @Test
    void getExpRequiredForLevel_shouldReturnCorrectExpForLevel3() {
        // Level 3: 110 * 1.1 = 121
        assertEquals(121L, experienceService.getExpRequiredForLevel(3));
    }

    @Test
    void getExpRequiredForLevel_shouldThrowException_forInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> experienceService.getExpRequiredForLevel(0));
        assertThrows(IllegalArgumentException.class, () -> experienceService.getExpRequiredForLevel(101));
    }

    // ========== Testes de Ganho de Experiência ==========

    @Test
    void gainExperience_shouldAddExpWithoutLevelUp() {
        GainExpRequest request = new GainExpRequest(50L, "Missão simples", "Test Source");
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character updated = experienceService.gainExperience(characterId, request);

        assertEquals(1, updated.getLevel());
        assertEquals(50L, updated.getExp());
        assertEquals(0, updated.getTp());
        verify(characterRepository, times(1)).save(testCharacter);
    }

    @Test
    void gainExperience_shouldLevelUpOnce() {
        // XP necessário para Level 2 é 110
        testCharacter.setExp(60L); // 60 XP atual
        GainExpRequest request = new GainExpRequest(50L, "Missão que completa o level", "Test Source"); // 60 + 50 = 110

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character updated = experienceService.gainExperience(characterId, request);

        assertEquals(2, updated.getLevel());
        assertEquals(0L, updated.getExp()); // 110 - 110 = 0
        assertEquals(5, updated.getTp()); // 5 TP de recompensa
        verify(characterRepository, times(1)).save(testCharacter);
    }

    @Test
    void gainExperience_shouldLevelUpMultipleTimes() {
        // Level 1 -> 2: 110 XP
        // Level 2 -> 3: 121 XP
        // Total para Level 3: 110 + 121 = 231 XP
        testCharacter.setExp(0L);
        GainExpRequest request = new GainExpRequest(250L, "Missão épica", "Test Source");

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));
        when(characterRepository.save(any(Character.class))).thenReturn(testCharacter);

        Character updated = experienceService.gainExperience(characterId, request);

        assertEquals(3, updated.getLevel());
        assertEquals(19L, updated.getExp()); // 250 - 231 = 19
        assertEquals(10, updated.getTp()); // 2 levels * 5 TP = 10
        verify(characterRepository, times(1)).save(testCharacter);
    }

    // ========== Testes de Progresso ==========

    @Test
    void getLevelProgress_shouldReturnCorrectProgress() {
        // XP necessário para Level 2 é 110
        testCharacter.setExp(55L);
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        LevelProgress progress = experienceService.getLevelProgress(characterId);

        assertEquals(55L, progress.currentExp());
        assertEquals(110L, progress.expForNextLevel());
        assertEquals(50.0, progress.progressPercentage());
        assertFalse(progress.isMaxLevel());
    }

    @Test
    void getLevelProgress_shouldReturnMaxLevelProgress() {
        testCharacter.setLevel(100);
        testCharacter.setExp(9999L);
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(testCharacter));

        LevelProgress progress = experienceService.getLevelProgress(characterId);

        assertEquals(0L, progress.currentExp());
        assertEquals(0L, progress.expForNextLevel());
        assertEquals(100.0, progress.progressPercentage());
        assertTrue(progress.isMaxLevel());
    }
}
