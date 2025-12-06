package com.mugen.backend.service;

import com.mugen.backend.dto.GainExpRequest;
import com.mugen.backend.entity.Character;
import com.mugen.backend.repository.CharacterRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceService {

    private final CharacterRepository characterRepository;

    // ==================== CONSTANTES DE PROGRESSÃO ====================
    
    // Experiência necessária para level 1
    private static final long BASE_EXP_FOR_LEVEL = 100;
    
    // Multiplicador progressivo (cada level requer mais XP)
    // Level 1 -> 100 XP
    // Level 2 -> 100 * 1.1 = 110 XP
    // Level 3 -> 110 * 1.1 = 121 XP
    // etc...
    private static final double EXP_MULTIPLIER = 1.1;
    
    // Limite máximo de level
    private static final int MAX_LEVEL = 100;
    
    // Bônus de XP por classe/raça (implementar depois)
    private static final double XP_BONUS_MULTIPLIER = 1.0;

    /**
     * Ganhar experiência e calcular level up automático
     */
    @Transactional
    public Character gainExperience(UUID characterId, GainExpRequest request) {
        log.info("Character {} gaining {} experience for reason: {}",
                characterId, request.getAmount(), request.getReason());

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        // Aplicar bônus de XP (se houver)
        long expToAdd = Math.round(request.getAmount() * XP_BONUS_MULTIPLIER);

        // Adicionar experiência
        long newExp = character.getExp() + expToAdd;

        log.info("Character {} current XP: {}, adding: {}, new total: {}",
                character.getName(), character.getExp(), expToAdd, newExp);

        // Calcular level ups
        int currentLevel = character.getLevel();
        int newLevel = currentLevel;
        long currentLevelExp = newExp;

        // Continuar aumentando level enquanto houver XP suficiente
        while (newLevel < MAX_LEVEL && currentLevelExp >= getExpRequiredForLevel(newLevel + 1)) {
            currentLevelExp -= getExpRequiredForLevel(newLevel + 1);
            newLevel++;
            log.info("Character {} leveled up to level {}! Remaining XP: {}", 
                    character.getName(), newLevel, currentLevelExp);
        }

        // Aplicar mudanças
        character.setExp(currentLevelExp);
        character.setLevel(newLevel);

        // Se houve level up, incrementar TP como recompensa
        if (newLevel > currentLevel) {
            int levelUpsCount = newLevel - currentLevel;
            int tpReward = levelUpsCount * 5; // 5 TP por level
            character.setTp(character.getTp() + tpReward);
            log.info("Character {} received {} TP as level up reward", 
                    character.getName(), tpReward);
        }

        Character saved = characterRepository.save(character);

        log.info("Experience gained for {}. New level: {}, New XP: {}, New TP: {}",
                saved.getName(), saved.getLevel(), saved.getExp(), saved.getTp());

        return saved;
    }

    /**
     * Calcula experiência necessária para chegar a um level específico
     * Usa progressão exponencial
     */
    public long getExpRequiredForLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Invalid level: " + level + " (must be 1-" + MAX_LEVEL + ")");
        }

        long expRequired = BASE_EXP_FOR_LEVEL;
        
        // Aplicar multiplicador para cada level acima de 1
        for (int i = 1; i < level; i++) {
            expRequired = Math.round(expRequired * EXP_MULTIPLIER);
        }

        return expRequired;
    }

    /**
     * Calcula experiência total necessária desde level 1 até um level específico
     */
    public long getTotalExpForLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("Invalid level: " + level + " (must be 1-" + MAX_LEVEL + ")");
        }

        long totalExp = 0;

        for (int i = 1; i < level; i++) {
            totalExp += getExpRequiredForLevel(i);
        }

        return totalExp;
    }

    /**
     * Obtém progresso de XP para o próximo level
     * Retorna [XP atual no level, XP necessário para próximo level, percentual]
     */
    public LevelProgress getLevelProgress(UUID characterId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        int currentLevel = character.getLevel();
        long currentExp = character.getExp();

        if (currentLevel >= MAX_LEVEL) {
            return new LevelProgress(0, 0, 100.0, true);
        }

        long expForNextLevel = getExpRequiredForLevel(currentLevel + 1);
        double progressPercentage = (currentExp * 100.0) / expForNextLevel;

        return new LevelProgress(
                currentExp,
                expForNextLevel,
                Math.min(progressPercentage, 100.0),
                false
        );
    }

    /**
     * Define o level do personagem (admin/debug)
     * Reseta XP para 0
     */
    @Transactional
    public Character setLevel(UUID characterId, int newLevel) {
        if (newLevel < 1 || newLevel > MAX_LEVEL) {
            throw new IllegalArgumentException("Invalid level: " + newLevel + " (must be 1-" + MAX_LEVEL + ")");
        }

        log.warn("Setting character {} level to {} (admin action)", characterId, newLevel);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        character.setLevel(newLevel);
        character.setExp(0L);

        Character saved = characterRepository.save(character);
        log.info("Character level set to {}", newLevel);

        return saved;
    }

    /**
     * Reseta XP e Level (admin/debug)
     */
    @Transactional
    public Character resetExperience(UUID characterId) {
        log.warn("Resetting experience for character {} (admin action)", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        character.setLevel(1);
        character.setExp(0L);

        Character saved = characterRepository.save(character);
        log.info("Character experience reset");

        return saved;
    }

    // ==================== CLASSE AUXILIAR ====================

    /**
     * DTO para retornar progresso de XP
     */
    @Getter
    public static class LevelProgress {
        private final long currentExp;
        private final long expForNextLevel;
        private final double progressPercentage;
        private final boolean isMaxLevel;

        public LevelProgress(long currentExp, long expForNextLevel, double progressPercentage, boolean isMaxLevel) {
            this.currentExp = currentExp;
            this.expForNextLevel = expForNextLevel;
            this.progressPercentage = progressPercentage;
            this.isMaxLevel = isMaxLevel;
        }

    }
}
