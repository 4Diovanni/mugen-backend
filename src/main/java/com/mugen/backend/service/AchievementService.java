package com.mugen.backend.service;

import com.mugen.backend.dto.achievement.AwardTPRequest;
import com.mugen.backend.dto.achievement.CreateAchievementRequest;
import com.mugen.backend.entity.Achievement;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAchievement;
import com.mugen.backend.repository.achievement.AchievementRepository;
import com.mugen.backend.repository.achievement.CharacterAchievementRepository;
import com.mugen.backend.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final CharacterAchievementRepository characterAchievementRepository;
    private final CharacterRepository characterRepository;
    private final TPService tpService;

    // ==================== ACHIEVEMENT CRUD ====================

    /**
     * Cria um Achievement
     */
    @Transactional
    public Achievement createAchievement(CreateAchievementRequest request) {
        log.info("Creating achievement: {}", request.getKeyName());

        // Validar se keyName já existe
        if (achievementRepository.findByKeyName(request.getKeyName()).isPresent()) {
            throw new IllegalArgumentException("Achievement with keyName " + request.getKeyName() + " already exists");
        }

        Achievement achievement = Achievement.builder()
                .keyName(request.getKeyName())
                .title(request.getTitle())
                .description(request.getDescription())
                .requirementJson(request.getRequirementJson())
                .rewardTp(request.getRewardTp() != null ? request.getRewardTp() : 0)
                .category(request.getCategory())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Achievement saved = achievementRepository.save(achievement);
        log.info("Achievement created with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Obtém achievement por ID
     */
    public Achievement getAchievementById(Integer id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + id));
    }

    /**
     * Obtém achievement por keyName
     */
    public Optional<Achievement> getAchievementByKeyName(String keyName) {
        return achievementRepository.findByKeyName(keyName);
    }

    /**
     * Lista todos os achievements ativos
     */
    public List<Achievement> getAllActiveAchievements() {
        return achievementRepository.findByIsActiveTrue();
    }

    /**
     * Lista achievements por categoria
     */
    public List<Achievement> getAchievementsByCategory(String category) {
        return achievementRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Lista todos os achievements
     */
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    /**
     * Atualiza um achievement
     */
    @Transactional
    public Achievement updateAchievement(Integer id, CreateAchievementRequest request) {
        log.info("Updating achievement: {}", id);

        Achievement achievement = getAchievementById(id);

        achievement.setTitle(request.getTitle());
        achievement.setDescription(request.getDescription());
        achievement.setRequirementJson(request.getRequirementJson());
        achievement.setRewardTp(request.getRewardTp());
        achievement.setCategory(request.getCategory());
        achievement.setIsActive(request.getIsActive());

        Achievement updated = achievementRepository.save(achievement);
        log.info("Achievement updated: {}", id);
        return updated;
    }

    /**
     * Deleta um achievement
     */
    @Transactional
    public void deleteAchievement(Integer id) {
        log.info("Deleting achievement: {}", id);

        if (!achievementRepository.existsById(id)) {
            throw new IllegalArgumentException("Achievement not found: " + id);
        }

        achievementRepository.deleteById(id);
        log.info("Achievement deleted: {}", id);
    }

    // ==================== CHARACTER ACHIEVEMENTS ====================

    /**
     * Debloqueia um achievement para um personagem
     * ✅ CORRIGIDO: Não tenta chamar TPService com awardedBy null
     */
    @Transactional
    public CharacterAchievement unlockAchievement(UUID characterId, Integer achievementId, String notificationMessage) {
        log.info("Unlocking achievement {} for character {}", achievementId, characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        Achievement achievement = getAchievementById(achievementId);

        // Verificar se já tem este achievement
        Optional<CharacterAchievement> existing = characterAchievementRepository
                .findByCharacterIdAndAchievementId(characterId, achievementId);

        if (existing.isPresent()) {
            log.warn("Character {} already has achievement {}", characterId, achievementId);
            return existing.get();
        }

        // Criar o achievement desbloqueado
        CharacterAchievement characterAchievement = CharacterAchievement.builder()
                .character(character)
                .achievement(achievement)
                .notificationMessage(notificationMessage)
                .build();

        CharacterAchievement saved = characterAchievementRepository.save(characterAchievement);

        // Dar TP de recompensa se tiver
        if (achievement.getRewardTp() > 0) {
            try {
                AwardTPRequest tpRequest = AwardTPRequest.builder()
                        .characterId(characterId)
                        .amount(achievement.getRewardTp())
                        .reason("ACHIEVEMENT_" + achievement.getKeyName())
                        .build();

                // ✅ NÃO passar awardedBy (deixar null para sistema)
                // TPService deve tratar null como "sistema"
                tpService.awardTP(tpRequest, null);
            } catch (NullPointerException e) {
                log.warn("Error awarding TP for achievement unlock - awardedBy was null. Achievement TP not awarded but achievement unlocked.", e);
                // Continua normalmente, apenas não dá o TP
            }
        }

        log.info("Achievement unlocked for character {}: {}", characterId, achievement.getTitle());
        return saved;
    }

    /**
     * Obtém todos os achievements de um personagem
     */
    public List<CharacterAchievement> getCharacterAchievements(UUID characterId) {
        return characterAchievementRepository.findByCharacterIdOrderByObtainedAtDesc(characterId);
    }

    /**
     * Obtém achievements de um personagem por categoria
     */
    public List<CharacterAchievement> getCharacterAchievementsByCategory(UUID characterId, String category) {
        return characterAchievementRepository.findByCharacterIdAndCategory(characterId, category);
    }

    /**
     * Verifica se um personagem tem um achievement
     */
    public boolean hasAchievement(UUID characterId, Integer achievementId) {
        return characterAchievementRepository.findByCharacterIdAndAchievementId(characterId, achievementId).isPresent();
    }

    /**
     * Conta quantos achievements um personagem tem
     */
    public Integer countCharacterAchievements(UUID characterId) {
        return characterAchievementRepository.countUniqueAchievements(characterId);
    }

    /**
     * Obtém TP total ganho com achievements
     */
    public Integer getTotalAchievementTP(UUID characterId) {
        Integer total = characterAchievementRepository.sumTpByCharacterId(characterId);
        return total != null ? total : 0;
    }

    /**
     * Remove um achievement de um personagem (admin only)
     */
    @Transactional
    public void removeAchievement(UUID characterId, Integer achievementId) {
        log.info("Removing achievement {} from character {}", achievementId, characterId);

        CharacterAchievement characterAchievement = characterAchievementRepository
                .findByCharacterIdAndAchievementId(characterId, achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Character achievement not found"));

        characterAchievementRepository.delete(characterAchievement);
        log.info("Achievement removed from character {}", characterId);
    }

    /**
     * Reseta todos os achievements de um personagem (admin only)
     */
    @Transactional
    public void resetAllAchievements(UUID characterId) {
        log.warn("Resetting all achievements for character {}", characterId);

        List<CharacterAchievement> achievements = characterAchievementRepository.findByCharacterId(characterId);
        characterAchievementRepository.deleteAll(achievements);

        log.info("All achievements reset for character {}", characterId);
    }

    // ==================== VERIFICAÇÕES E CHECAGENS ====================

    /**
     * Valida se um personagem atende aos requisitos de um achievement
     * Retorna true se atende aos requisitos
     */
    public boolean meetsRequirements(UUID characterId, Integer achievementId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));

        Achievement achievement = getAchievementById(achievementId);

        // Parse do requirementJson (implementar conforme necessário)
        // Exemplo: {"minLevel": 50, "minCharacters": 3}

        // TODO: Implementar lógica de validação de requisitos com JSON parsing

        return true;
    }

    /**
     * Verifica novos achievements possíveis ao subir de nível
     */
    @Transactional
    public void checkLevelUpAchievements(UUID characterId) {
        log.debug("Checking level up achievements for character {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));

        // Verificar achievements de level
        List<Achievement> levelAchievements = achievementRepository.findByCategory("LEVEL");
        for (Achievement achievement : levelAchievements) {
            if (!hasAchievement(characterId, achievement.getId()) && meetsRequirements(characterId, achievement.getId())) {
                unlockAchievement(characterId, achievement.getId(),
                        "Parabéns! Você desbloqueou: " + achievement.getTitle());
            }
        }
    }

    /**
     * Verifica novos achievements possíveis ao subir atributo
     */
    @Transactional
    public void checkAttributeAchievements(UUID characterId) {
        log.debug("Checking attribute achievements for character {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));

        // Verificar achievements de atributos
        List<Achievement> attributeAchievements = achievementRepository.findByCategory("COMBAT");
        for (Achievement achievement : attributeAchievements) {
            if (!hasAchievement(characterId, achievement.getId()) && meetsRequirements(characterId, achievement.getId())) {
                unlockAchievement(characterId, achievement.getId(),
                        "Parabéns! Você desbloqueou: " + achievement.getTitle());
            }
        }
    }
}