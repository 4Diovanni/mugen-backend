package com.mugen.backend.controller;

import com.mugen.backend.dto.CreateAchievementRequest;
import com.mugen.backend.entity.Achievement;
import com.mugen.backend.entity.CharacterAchievement;
import com.mugen.backend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
@Slf4j
public class AchievementController {

    private final AchievementService achievementService;

    // ==================== ACHIEVEMENT CRUD ====================

    /**
     * POST /api/achievements - Cria um novo achievement
     * Admin only
     */
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody CreateAchievementRequest request) {
        log.info("POST /achievements - Creating achievement: {}", request.getKeyName());
        Achievement achievement = achievementService.createAchievement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(achievement);
    }

    /**
     * GET /api/achievements - Lista todos os achievements
     */
    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        log.info("GET /achievements - Fetching all achievements");
        List<Achievement> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/active - Lista apenas achievements ativos
     */
    @GetMapping("/active")
    public ResponseEntity<List<Achievement>> getActiveAchievements() {
        log.info("GET /achievements/active - Fetching active achievements");
        List<Achievement> achievements = achievementService.getAllActiveAchievements();
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/category/{category} - Lista por categoria
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Achievement>> getAchievementsByCategory(@PathVariable String category) {
        log.info("GET /achievements/category/{} - Fetching", category);
        List<Achievement> achievements = achievementService.getAchievementsByCategory(category);
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/{id} - Obtém um achievement específico
     */
    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Integer id) {
        log.info("GET /achievements/{} - Fetching", id);
        Achievement achievement = achievementService.getAchievementById(id);
        return ResponseEntity.ok(achievement);
    }

    /**
     * GET /api/achievements/key/{keyName} - Obtém por keyName
     */
    @GetMapping("/key/{keyName}")
    public ResponseEntity<Achievement> getAchievementByKeyName(@PathVariable String keyName) {
        log.info("GET /achievements/key/{} - Fetching", keyName);
        Achievement achievement = achievementService.getAchievementByKeyName(keyName)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + keyName));
        return ResponseEntity.ok(achievement);
    }

    /**
     * PUT /api/achievements/{id} - Atualiza um achievement
     * Admin only
     */
    @PutMapping("/{id}")
    public ResponseEntity<Achievement> updateAchievement(
            @PathVariable Integer id,
            @RequestBody CreateAchievementRequest request) {
        log.info("PUT /achievements/{} - Updating", id);
        Achievement achievement = achievementService.updateAchievement(id, request);
        return ResponseEntity.ok(achievement);
    }

    /**
     * DELETE /api/achievements/{id} - Deleta um achievement
     * Admin only
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Integer id) {
        log.info("DELETE /achievements/{} - Deleting", id);
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CHARACTER ACHIEVEMENTS ====================

    /**
     * GET /api/achievements/character/{characterId} - Lista achievements do character
     */
    @GetMapping("/character/{characterId}")
    public ResponseEntity<List<CharacterAchievement>> getCharacterAchievements(@PathVariable UUID characterId) {
        log.info("GET /achievements/character/{} - Fetching achievements", characterId);
        List<CharacterAchievement> achievements = achievementService.getCharacterAchievements(characterId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/character/{characterId}/category/{category} - Achievements por categoria
     */
    @GetMapping("/character/{characterId}/category/{category}")
    public ResponseEntity<List<CharacterAchievement>> getCharacterAchievementsByCategory(
            @PathVariable UUID characterId,
            @PathVariable String category) {
        log.info("GET /achievements/character/{}/category/{} - Fetching", characterId, category);
        List<CharacterAchievement> achievements =
                achievementService.getCharacterAchievementsByCategory(characterId, category);
        return ResponseEntity.ok(achievements);
    }

    /**
     * POST /api/achievements/character/{characterId}/unlock/{achievementId} - Debloqueia um achievement
     * Admin/System only
     */
    @PostMapping("/character/{characterId}/unlock/{achievementId}")
    public ResponseEntity<CharacterAchievement> unlockAchievement(
            @PathVariable UUID characterId,
            @PathVariable Integer achievementId,
            @RequestParam(required = false) String message) {
        log.info("POST /achievements/character/{}/unlock/{} - Unlocking", characterId, achievementId);
        String notification = message != null ? message :
                "Parabéns! Você desbloqueou um novo achievement!";
        CharacterAchievement achievement =
                achievementService.unlockAchievement(characterId, achievementId, notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(achievement);
    }

    /**
     * DELETE /api/achievements/character/{characterId}/achievement/{achievementId} - Remove achievement
     * Admin only
     */
    @DeleteMapping("/character/{characterId}/achievement/{achievementId}")
    public ResponseEntity<Void> removeAchievement(
            @PathVariable UUID characterId,
            @PathVariable Integer achievementId) {
        log.info("DELETE /achievements/character/{}/achievement/{} - Removing", characterId, achievementId);
        achievementService.removeAchievement(characterId, achievementId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/achievements/character/{characterId}/reset - Reseta todos os achievements
     * Admin only
     */
    @DeleteMapping("/character/{characterId}/reset")
    public ResponseEntity<Void> resetAllAchievements(@PathVariable UUID characterId) {
        log.info("DELETE /achievements/character/{}/reset - Resetting all", characterId);
        achievementService.resetAllAchievements(characterId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/achievements/character/{characterId}/count - Conta achievements do character
     */
    @GetMapping("/character/{characterId}/count")
    public ResponseEntity<Integer> countCharacterAchievements(@PathVariable UUID characterId) {
        log.info("GET /achievements/character/{}/count - Counting", characterId);
        Integer count = achievementService.countCharacterAchievements(characterId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/achievements/character/{characterId}/tp - TP ganho com achievements
     */
    @GetMapping("/character/{characterId}/tp")
    public ResponseEntity<Integer> getTotalAchievementTP(@PathVariable UUID characterId) {
        log.info("GET /achievements/character/{}/tp - Fetching total TP", characterId);
        Integer totalTP = achievementService.getTotalAchievementTP(characterId);
        return ResponseEntity.ok(totalTP);
    }

    /**
     * GET /api/achievements/character/{characterId}/has/{achievementId} - Verifica se tem achievement
     */
    @GetMapping("/character/{characterId}/has/{achievementId}")
    public ResponseEntity<Boolean> hasAchievement(
            @PathVariable UUID characterId,
            @PathVariable Integer achievementId) {
        log.info("GET /achievements/character/{}/has/{} - Checking", characterId, achievementId);
        boolean has = achievementService.hasAchievement(characterId, achievementId);
        return ResponseEntity.ok(has);
    }


}