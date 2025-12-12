package com.mugen.backend.controller;

import com.mugen.backend.dto.character.CharacterDTO;
import com.mugen.backend.dto.character.CharacterStats;
import com.mugen.backend.dto.character.UpdateCharacterDTO;
import com.mugen.backend.dto.character.UpdateCharacterNameDTO;
import com.mugen.backend.dto.tp.ExperienceInfo;
import com.mugen.backend.dto.tp.ExperienceTable;
import com.mugen.backend.dto.tp.GainExpRequest;

import com.mugen.backend.entity.*;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.entity.character.CharacterSkill;
import com.mugen.backend.entity.character.CharacterTransformation;

import com.mugen.backend.service.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.mugen.backend.dto.tp.AllocateAttributeRequest;
import com.mugen.backend.dto.achievement.AwardTPRequest;
import com.mugen.backend.dto.tp.TPSummary;
import com.mugen.backend.entity.TPTransaction;
import com.mugen.backend.service.ExperienceService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Character Controller - API para gerenciar personagens
 * Organização:
 * 1. ✅ CRUD Básico (Create, Read, Update, Delete)
 * 2. 🔧 Sub-recursos mais específicos (Skills, Stats, Atributos)
 * 3. 📋 Listagem e Consultas (Genéricas)
 * 4. ⚡ Sistema de TP
 * 5. 📊 Experiência e Leveling
 */
@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
@Slf4j
public class CharacterController {

    private final CharacterService characterService;
    private final CharacterStatService statService;
    private final TPService tpService;
    private final TransformationService transformationService;
    private final ExperienceService experienceService;

    // ==================== HELPER METHODS ====================

    /**
     * ✅ Método auxiliar para obter userId autenticado
     * Reutilizable em todos os endpoints que precisam validar autenticação
     */
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ Unauthorized access attempt");
            throw new IllegalArgumentException("User not authenticated");
        }

        return (String) authentication.getPrincipal();
    }

    // ==================== 1️⃣ CRUD BÁSICO ====================

    /**
     * POST /api/characters
     * Criar novo personagem
     */
    @PostMapping
    public ResponseEntity<Character> createCharacter(@Valid @RequestBody CharacterDTO dto) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} creating character: {}", userId, dto.getName());
        Character created = characterService.createCharacter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/characters/{id}
     * Buscar personagem por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.debug("✅ User {} requesting character: {}", userId, id);
        return characterService.findByIdWithFullDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/characters/{id}
     * Atualizar personagem completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterDTO dto) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} updating character: {}", userId, id);
        Character updated = characterService.updateCharacter(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/characters/{id}
     * Deletar personagem (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} deleting character: {}", userId, id);
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 2️⃣ SUB-RECURSOS ESPECÍFICOS ====================

    // --- Transformações ---

    /**
     * GET /api/characters/{characterId}/transformations/available
     * Listar transformações disponíveis para desbloquear
     */
    @GetMapping("/{characterId}/transformations/available")
    public ResponseEntity<List<Transformation>> getAvailableTransformations(
            @PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting available transformations for character {}", userId, characterId);
        List<Transformation> transformations = transformationService.getAvailableTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * POST /api/characters/{characterId}/transformations/{transformationId}
     * Desbloquear transformação para personagem
     */
    @PostMapping("/{characterId}/transformations/{transformationId}")
    public ResponseEntity<CharacterTransformation> unlockTransformation(
            @PathVariable UUID characterId,
            @PathVariable Integer transformationId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} unlocking transformation {} for character {}", userId, transformationId, characterId);
        CharacterTransformation transformation = transformationService.unlockTransformation(characterId, transformationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(transformation);
    }

    /**
     * GET /api/characters/{characterId}/transformations
     * Listar todas as transformações do personagem
     */
    @GetMapping("/{characterId}/transformations")
    public ResponseEntity<List<CharacterTransformation>> getCharacterTransformations(
            @PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting transformations for character {}", userId, characterId);
        List<CharacterTransformation> transformations = transformationService.getCharacterTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * GET /api/characters/{characterId}/transformations/unlocked
     * Listar apenas transformações desbloqueadas
     */
    @GetMapping("/{characterId}/transformations/unlocked")
    public ResponseEntity<List<CharacterTransformation>> getUnlockedTransformations(
            @PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting unlocked transformations for character {}", userId, characterId);
        List<CharacterTransformation> transformations = transformationService.getUnlockedTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * GET /api/characters/{characterId}/transformations/{transformationId}/unlocked
     * Verificar se personagem tem transformação específica desbloqueada
     */
    @GetMapping("/{characterId}/transformations/{transformationId}/unlocked")
    public ResponseEntity<Boolean> hasUnlockedTransformation(
            @PathVariable UUID characterId,
            @PathVariable Integer transformationId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} checking transformation {} for character {}", userId, transformationId, characterId);
        boolean unlocked = transformationService.hasUnlockedTransformation(characterId, transformationId);
        return ResponseEntity.ok(unlocked);
    }

    // --- Skills ---

    /**
     * POST /api/characters/{characterId}/skills/{skillId}
     * Adicionar skill ao personagem
     */
    @PostMapping("/{characterId}/skills/{skillId}")
    public ResponseEntity<CharacterSkill> addSkillToCharacter(
            @PathVariable UUID characterId,
            @PathVariable Integer skillId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} adding skill {} to character {}", userId, skillId, characterId);
        CharacterSkill characterSkill = characterService.addSkillToCharacter(characterId, skillId);
        return ResponseEntity.status(HttpStatus.CREATED).body(characterSkill);
    }

    /**
     * GET /api/characters/{characterId}/skills
     * Listar skills do personagem
     */
    @GetMapping("/{characterId}/skills")
    public ResponseEntity<List<CharacterSkill>> getCharacterSkills(@PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting skills for character {}", userId, characterId);
        List<CharacterSkill> skills = characterService.getCharacterSkills(characterId);
        return ResponseEntity.ok(skills);
    }

    /**
     * DELETE /api/characters/{characterId}/skills/{skillId}
     * Remover skill do personagem
     */
    @DeleteMapping("/{characterId}/skills/{skillId}")
    public ResponseEntity<Void> removeSkillFromCharacter(
            @PathVariable UUID characterId,
            @PathVariable Integer skillId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} removing skill {} from character {}", userId, skillId, characterId);
        characterService.removeSkillFromCharacter(characterId, skillId);
        return ResponseEntity.noContent().build();
    }

    // --- Stats e Atributos ---

    /**
     * GET /api/characters/{id}/stats
     * Calcular stats finais do personagem
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CharacterStats> getCharacterStats(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.debug("✅ User {} calculating stats for character: {}", userId, id);
        return characterService.findByIdWithFullDetails(id)
                .map(statService::calculateStats)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Atualizações Parciais ---

    /**
     * PATCH /api/characters/{id}/name
     * Atualizar apenas o nome do personagem
     */
    @PatchMapping("/{id}/name")
    public ResponseEntity<Character> updateCharacterName(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterNameDTO dto) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} updating character {} name to {}", userId, id, dto.getName());
        Character updated = characterService.updateCharacterName(id, dto.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/characters/{id}/deactivate
     * Desativar personagem (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Character> deactivateCharacter(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} deactivating character: {}", userId, id);
        characterService.softDeleteCharacter(id);
        Character character = characterService.getCharacterById(id);
        return ResponseEntity.ok(character);
    }

    // ==================== 3️⃣ LISTAGEM E CONSULTAS ====================

    /**
     * GET /api/characters
     * Listar todos os personagens com paginação
     */
    @GetMapping
    public ResponseEntity<Page<Character>> getAllCharacters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} requesting characters (page: {}, size: {}, sortBy: {}, direction: {})",
                userId, page, size, sortBy, sortDirection);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Character> characters = characterService.getAllCharacters(pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /api/characters/owner/{ownerId}
     * Listar personagens de um usuário (sem paginação)
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Character>> getCharactersByOwnerId(@PathVariable UUID ownerId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting characters by owner id: {}", userId, ownerId);
        List<Character> characters = characterService.getCharactersByOwner(ownerId);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /api/characters/owner/{ownerId}/paginated
     * Listar personagens de um usuário com paginação
     */
    @GetMapping("/owner/{ownerId}/paginated")
    public ResponseEntity<Page<Character>> getCharactersByOwnerIdPaginated(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} requesting paginated characters by owner id: {}", userId, ownerId);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Character> characters = characterService.getCharactersByOwnerPaginated(ownerId, pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /api/characters/owner/{ownerId}/count
     * Contar personagens de um usuário
     */
    @GetMapping("/owner/{ownerId}/count")
    public ResponseEntity<Long> countCharactersByOwnerId(@PathVariable UUID ownerId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} counting characters by owner id: {}", userId, ownerId);
        long count = characterService.countByOwnerId(ownerId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/characters/{id}/exists
     * Verificar se personagem existe
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> characterExists(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} checking if character exists: {}", userId, id);
        boolean exists = characterService.characterExists(id);
        return ResponseEntity.ok(exists);
    }

    // ==================== 4️⃣ TP SYSTEM ====================

    /**
     * POST /api/characters/{id}/allocate-attribute
     * Alocar pontos em um atributo (gastar TP)
     */
    @PostMapping("/{id}/allocate-attribute")
    public ResponseEntity<Character> allocateAttribute(
            @PathVariable UUID id,
            @Valid @RequestBody AllocateAttributeRequest request) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} allocating {} points to {} for character {}",
                userId, request.getPoints(), request.getAttributeName(), id);
        User mockUser = User.builder().id(UUID.fromString(userId)).build();
        Character updated = tpService.allocateAttribute(id, request, mockUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/characters/{id}/award-tp
     * Conceder TP ao personagem
     */
    @PostMapping("/{id}/award-tp")
    public ResponseEntity<Character> awardTP(
            @PathVariable UUID id,
            @Valid @RequestBody AwardTPRequest request) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} awarding {} TP to character {} - Reason: {}",
                userId, request.getAmount(), id, request.getReason());
        Character character = characterService.getCharacterById(id);
        User awardedBy = character.getOwner();
        if (!id.equals(request.getCharacterId())) {
            throw new IllegalArgumentException("Character ID mismatch");
        }
        Character updated = tpService.awardTP(request, awardedBy);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/characters/{id}/tp-summary
     * Obter resumo de TP do personagem
     */
    @GetMapping("/{id}/tp-summary")
    public ResponseEntity<TPSummary> getTPSummary(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting TP summary for character {}", userId, id);
        TPSummary summary = tpService.getTPSummary(id);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/characters/{id}/tp-cost/{attributeName}/{points}
     * Calcular custo de TP para alocar pontos
     */
    @GetMapping("/{id}/tp-cost/{attributeName}/{points}")
    public ResponseEntity<Integer> calculateTPCost(
            @PathVariable UUID id,
            @PathVariable String attributeName,
            @PathVariable Integer points) {
        String userId = getAuthenticatedUserId();
        log.debug("✅ User {} calculating TP cost for {} points in {} for character {}",
                userId, points, attributeName, id);
        if (!characterService.characterExists(id)) {
            return ResponseEntity.notFound().build();
        }
        if (!attributeName.matches("^(STR|DEX|CON|WIL|MND|SPI)$")) {
            return ResponseEntity.badRequest().build();
        }
        if (points < 1 || points > 50) {
            return ResponseEntity.badRequest().build();
        }
        Character character = characterService.getCharacterById(id);
        CharacterAttribute attr = character.getAttributes();
        int currentValue = switch (attributeName.toUpperCase()) {
            case "STR" -> attr.getStr();
            case "DEX" -> attr.getDex();
            case "CON" -> attr.getCon();
            case "WIL" -> attr.getWil();
            case "MND" -> attr.getMnd();
            case "SPI" -> attr.getSpi();
            default -> 0;
        };
        int cost = tpService.calculateAttributeCost(currentValue, points);
        return ResponseEntity.ok(cost);
    }

    /**
     * GET /api/characters/{id}/tp-history
     * Obter histórico de transações de TP do personagem
     */
    @GetMapping("/{id}/tp-history")
    public ResponseEntity<List<TPTransaction>> getTPHistory(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting TP history for character {}", userId, id);
        if (!characterService.characterExists(id)) {
            return ResponseEntity.notFound().build();
        }
        List<TPTransaction> history = tpService.getTransactionHistory(id);
        return ResponseEntity.ok(history);
    }

    // ==================== 5️⃣ EXPERIÊNCIA E LEVELING ====================

    /**
     * POST /api/characters/{characterId}/gain-exp
     * Ganhar experiência (calcula level up automático)
     */
    @PostMapping("/{characterId}/gain-exp")
    public ResponseEntity<Character> gainExperience(
            @PathVariable UUID characterId,
            @Valid @RequestBody GainExpRequest request) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} - Character {} gaining {} experience for reason: {}",
                userId, characterId, request.getAmount(), request.getReason());
        Character updated = experienceService.gainExperience(characterId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/characters/{characterId}/level-progress
     * Obter progresso de XP para o próximo level
     */
    @GetMapping("/{characterId}/level-progress")
    public ResponseEntity<ExperienceService.LevelProgress> getLevelProgress(@PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting level progress for character {}", userId, characterId);
        ExperienceService.LevelProgress progress = experienceService.getLevelProgress(characterId);
        return ResponseEntity.ok(progress);
    }

    /**
     * GET /api/characters/experience/exp-table
     * Tabela de experiência (quantos XP por level)
     */
    @GetMapping("/experience/exp-table")
    public ResponseEntity<List<ExperienceTable>> getExperienceTable(
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "20") int end) {
        String userId = getAuthenticatedUserId();
        if (start < 1 || end > 100 || start > end) {
            return ResponseEntity.badRequest().build();
        }
        log.info("✅ User {} getting experience table from level {} to {}", userId, start, end);
        List<ExperienceTable> table = new ArrayList<>();
        for (int level = start; level <= end; level++) {
            long expRequired = experienceService.getExpRequiredForLevel(level);
            long totalExp = experienceService.getTotalExpForLevel(level);
            table.add(new ExperienceTable(level, expRequired, totalExp));
        }
        return ResponseEntity.ok(table);
    }

    /**
     * GET /api/characters/{characterId}/exp-info
     * Informações completas de experiência do personagem
     */
    @GetMapping("/{characterId}/exp-info")
    public ResponseEntity<ExperienceInfo> getExperienceInfo(@PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.info("✅ User {} getting experience info for character {}", userId, characterId);
        Character character = characterService.getCharacterById(characterId);
        ExperienceService.LevelProgress progress = experienceService.getLevelProgress(characterId);
        long totalExp = experienceService.getTotalExpForLevel(character.getLevel());
        long nextLevelIn = progress.getExpForNextLevel() - progress.getCurrentExp();
        return ResponseEntity.ok(new ExperienceInfo(
                character.getLevel(),
                character.getExp(),
                progress.getExpForNextLevel(),
                progress.getProgressPercentage(),
                totalExp + character.getExp(),
                progress.isMaxLevel(),
                nextLevelIn
        ));
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * PATCH /api/characters/{characterId}/set-level/{level}
     * [ADMIN] Definir level do personagem (reset XP)
     */
    @PatchMapping("/{characterId}/set-level/{level}")
    public ResponseEntity<Character> setCharacterLevel(
            @PathVariable UUID characterId,
            @PathVariable int level) {
        String userId = getAuthenticatedUserId();
        log.warn("⚠️ ADMIN ACTION: User {} setting character {} level to {}", userId, characterId, level);
        Character updated = experienceService.setLevel(characterId, level);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/characters/{characterId}/reset-experience
     * [ADMIN] Reset ar XP e Level para 1
     */
    @PatchMapping("/{characterId}/reset-experience")
    public ResponseEntity<Character> resetExperience(@PathVariable UUID characterId) {
        String userId = getAuthenticatedUserId();
        log.warn("⚠️ ADMIN ACTION: User {} resetting experience for character {}", userId, characterId);
        Character updated = experienceService.resetExperience(characterId);
        return ResponseEntity.ok(updated);
    }
}