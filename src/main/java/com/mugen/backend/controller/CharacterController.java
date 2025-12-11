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

    // ==================== 1️⃣ CRUD BÁSICO ====================

    /**
     * POST /characters
     * Criar novo personagem
     */
    @PostMapping
    public ResponseEntity<Character> createCharacter(@Valid @RequestBody CharacterDTO dto) {
        log.info("Request to create character: {}", dto.getName());
        Character created = characterService.createCharacter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /characters/{id}
     * Buscar personagem por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable UUID id) {
        log.debug("Getting character: {}", id);
        return characterService.findByIdWithFullDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /characters/{id}
     * Atualizar personagem completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterDTO dto) {
        log.info("Request to update character: {}", id);
        Character updated = characterService.updateCharacter(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /characters/{id}
     * Deletar personagem (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
        log.info("Request to delete character: {}", id);
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 2️⃣ SUB-RECURSOS ESPECÍFICOS ====================
    // ==================== 3️⃣ TRANSFORMAÇÕES ====================


    /**
     * GET /characters/{characterId}/transformations/available
     * Listar transformações disponíveis para desbloquear
     */
    @GetMapping("/{characterId}/transformations/available")
    public ResponseEntity<List<Transformation>> getAvailableTransformations(
            @PathVariable UUID characterId) {
        log.info("Getting available transformations for character {}", characterId);

        List<Transformation> transformations = transformationService.getAvailableTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * POST /characters/{characterId}/transformations/{transformationId}
     * Desbloquear transformação para personagem
     */
    @PostMapping("/{characterId}/transformations/{transformationId}")
    public ResponseEntity<CharacterTransformation> unlockTransformation(
            @PathVariable UUID characterId,
            @PathVariable Integer transformationId) {
        log.info("Unlocking transformation {} for character {}", transformationId, characterId);

        CharacterTransformation transformation = transformationService.unlockTransformation(characterId, transformationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(transformation);
    }

    /**
     * GET /characters/{characterId}/transformations
     * Listar todas as transformações do personagem
     */
    @GetMapping("/{characterId}/transformations")
    public ResponseEntity<List<CharacterTransformation>> getCharacterTransformations(
            @PathVariable UUID characterId) {
        log.info("Getting transformations for character {}", characterId);

        List<CharacterTransformation> transformations = transformationService.getCharacterTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * GET /characters/{characterId}/transformations/unlocked
     * Listar apenas transformações desbloqueadas
     */
    @GetMapping("/{characterId}/transformations/unlocked")
    public ResponseEntity<List<CharacterTransformation>> getUnlockedTransformations(
            @PathVariable UUID characterId) {
        log.info("Getting unlocked transformations for character {}", characterId);

        List<CharacterTransformation> transformations = transformationService.getUnlockedTransformations(characterId);
        return ResponseEntity.ok(transformations);
    }

    /**
     * GET /characters/{characterId}/transformations/{transformationId}/unlocked
     * Verificar se personagem tem transformação específica desbloqueada
     */
    @GetMapping("/{characterId}/transformations/{transformationId}/unlocked")
    public ResponseEntity<Boolean> hasUnlockedTransformation(
            @PathVariable UUID characterId,
            @PathVariable Integer transformationId) {
        log.info("Checking if character {} has unlocked transformation {}", characterId, transformationId);

        boolean unlocked = transformationService.hasUnlockedTransformation(characterId, transformationId);
        return ResponseEntity.ok(unlocked);
    }
    // --- Skills ---

    /**
     * POST /characters/{characterId}/skills/{skillId}
     * Adicionar skill ao personagem
     */
    @PostMapping("/{characterId}/skills/{skillId}")
    public ResponseEntity<CharacterSkill> addSkillToCharacter(
            @PathVariable UUID characterId,
            @PathVariable Integer skillId) {
        log.info("Adding skill {} to character {}", skillId, characterId);
        CharacterSkill characterSkill = characterService.addSkillToCharacter(characterId, skillId);
        return ResponseEntity.status(HttpStatus.CREATED).body(characterSkill);
    }

    /**
     * GET /characters/{characterId}/skills
     * Listar skills do personagem
     */
    @GetMapping("/{characterId}/skills")
    public ResponseEntity<List<CharacterSkill>> getCharacterSkills(@PathVariable UUID characterId) {
        log.info("Getting skills for character {}", characterId);
        List<CharacterSkill> skills = characterService.getCharacterSkills(characterId);
        return ResponseEntity.ok(skills);
    }

    /**
     * DELETE /characters/{characterId}/skills/{skillId}
     * Remover skill do personagem
     */
    @DeleteMapping("/{characterId}/skills/{skillId}")
    public ResponseEntity<Void> removeSkillFromCharacter(
            @PathVariable UUID characterId,
            @PathVariable Integer skillId) {
        log.info("Removing skill {} from character {}", skillId, characterId);
        characterService.removeSkillFromCharacter(characterId, skillId);
        return ResponseEntity.noContent().build();
    }

    // --- Stats e Atributos ---

    /**
     * GET /characters/{id}/stats
     * Calcular stats finais do personagem
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CharacterStats> getCharacterStats(@PathVariable UUID id) {
        log.debug("Calculating stats for character: {}", id);
        return characterService.findByIdWithFullDetails(id)
                .map(statService::calculateStats)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    /**
//     * POST /characters/{id}/allocate-attribute
//     * Alocar pontos em atributo (gastar TP)
//     */
//    @PostMapping("/{id}/allocate-attribute")
//    public ResponseEntity<Character> allocateAttribute(
//            @PathVariable UUID id,
//            @RequestBody @Valid AllocateAttributeRequest request) {
//        log.info("Allocating {} points to {} for character {}",
//                request.getPoints(), request.getAttributeName(), id);
//        // TODO: Pegar user do contexto de segurança (quando implementar Spring Security)
//        User mockUser = User.builder().id(UUID.randomUUID()).build();
//        Character updated = tpService.allocateAttribute(id, request, mockUser);
//        return ResponseEntity.ok(updated);
//    }

    // --- Atualizações Parciais ---

    /**
     * PATCH /characters/{id}/name
     * Atualizar apenas o nome do personagem
     */
    @PatchMapping("/{id}/name")
    public ResponseEntity<Character> updateCharacterName(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterNameDTO dto) {
        log.info("Request to update character name: {} to {}", id, dto.getName());
        Character updated = characterService.updateCharacterName(id, dto.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /characters/{id}/deactivate
     * Desativar personagem (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Character> deactivateCharacter(@PathVariable UUID id) {
        log.info("Request to deactivate character: {}", id);
        characterService.softDeleteCharacter(id);
        Character character = characterService.getCharacterById(id);
        return ResponseEntity.ok(character);
    }

    // ==================== 3️⃣ LISTAGEM E CONSULTAS ====================

    /**
     * GET /characters
     * Listar todos os personagens com paginação
     */
    @GetMapping
    public ResponseEntity<Page<Character>> getAllCharacters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.info("Request to get all characters - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, sortDirection);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Character> characters = characterService.getAllCharacters(pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}
     * Listar personagens de um usuário (sem paginação)
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Character>> getCharactersByOwnerId(@PathVariable UUID ownerId) {
        log.info("Request to get characters by owner id: {}", ownerId);
        List<Character> characters = characterService.getCharactersByOwner(ownerId);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}/paginated
     * Listar personagens de um usuário com paginação
     */
    @GetMapping("/owner/{ownerId}/paginated")
    public ResponseEntity<Page<Character>> getCharactersByOwnerIdPaginated(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.info("Request to get paginated characters by owner id: {}", ownerId);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Character> characters = characterService.getCharactersByOwnerPaginated(ownerId, pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}/count
     * Contar personagens de um usuário
     */
    @GetMapping("/owner/{ownerId}/count")
    public ResponseEntity<Long> countCharactersByOwnerId(@PathVariable UUID ownerId) {
        log.info("Request to count characters by owner id: {}", ownerId);
        long count = characterService.countByOwnerId(ownerId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /characters/{id}/exists
     * Verificar se personagem existe
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> characterExists(@PathVariable UUID id) {
        log.info("Request to check if character exists: {}", id);
        boolean exists = characterService.characterExists(id);
        return ResponseEntity.ok(exists);
    }

    // ==================== 4️⃣ TP SYSTEM ====================

    /**
     * POST /characters/{id}/allocate-attribute
     * Alocar pontos em um atributo (gastar TP)
     * Validações:
     * - Mínimo 1 ponto, máximo 50 por alocação
     * - Máximo 120 por atributo
     * - Custo progressivo: Tier 1 (1-50) = 1 TP/ponto, Tier 2 (51-80) = 2 TP/ponto, Tier 3 (81-120) = 3 TP/ponto
     * - Deve ter TP suficiente
     */
    @PostMapping("/{id}/allocate-attribute")
    public ResponseEntity<Character> allocateAttribute(
            @PathVariable UUID id,
            @Valid @RequestBody AllocateAttributeRequest request) {
        log.info("Allocating {} points to {} for character {}",
                request.getPoints(), request.getAttributeName(), id);

        // TODO: Obter user do contexto de segurança (quando implementar Spring Security)
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        Character updated = tpService.allocateAttribute(id, request, mockUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /characters/{id}/award-tp
     * Conceder TP ao personagem (Minigame, Mestre, Evento, Achievement)
     * ⚠️ ADMIN ONLY - Requer autenticação e permissão de administrador
     * Casos de uso:
     * - Minigame: "MINIGAME_DICE_ROLL"
     * - Mestre: "MASTER_REWARD"
     * - Evento: "EVENT_TOURNAMENT"
     * - Achievement: "ACHIEVEMENT_FIRST_TRANSFORM"
     */
    @PostMapping("/{id}/award-tp")
    public ResponseEntity<Character> awardTP(
            @PathVariable UUID id,
            @Valid @RequestBody AwardTPRequest request) {

        log.info("Awarding {} TP to character {} - Reason: {}",
                request.getAmount(), id, request.getReason());

        // ✅ Buscar o character e usar o owner como awardedBy
        Character character = characterService.getCharacterById(id);
        User awardedBy = character.getOwner(); // Owner existe no banco!

        if (!id.equals(request.getCharacterId())) {
            throw new IllegalArgumentException("Character ID mismatch");
        }

        Character updated = tpService.awardTP(request, awardedBy);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /characters/{id}/tp-summary
     * Obter resumo de TP do personagem
     * Retorna:
     * - TP atual
     * - TP total ganhado na vida
     * - TP total gasto na vida
     * - Breakdown por categoria (minigames, master, eventos, atributos, skills, transformações)
     * - Taxa de utilização
     */
    @GetMapping("/{id}/tp-summary")
    public ResponseEntity<TPSummary> getTPSummary(@PathVariable UUID id) {
        log.info("Getting TP summary for character {}", id);

        TPSummary summary = tpService.getTPSummary(id);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /characters/{id}/tp-cost/{attributeName}/{points}
     * Calcular custo de TP para alocar pontos
     * Útil para frontend mostrar preview do custo antes de confirmar
     * Exemplos:
     * - GET /characters/uuid/tp-cost/STR/5 → Custo em TP
     * - GET /characters/uuid/tp-cost/DEX/10 → Custo em TP
     */
    @GetMapping("/{id}/tp-cost/{attributeName}/{points}")
    public ResponseEntity<Integer> calculateTPCost(
            @PathVariable UUID id,
            @PathVariable String attributeName,
            @PathVariable Integer points) {
        log.debug("Calculating TP cost for {} points in {} for character {}",
                points, attributeName, id);

        // Validar que o personagem existe
        if (!characterService.characterExists(id)) {
            return ResponseEntity.notFound().build();
        }

        // Validar atributo
        if (!attributeName.matches("^(STR|DEX|CON|WIL|MND|SPI)$")) {
            return ResponseEntity.badRequest().build();
        }

        // Validar pontos
        if (points < 1 || points > 50) {
            return ResponseEntity.badRequest().build();
        }

        // Buscar valor atual do atributo
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
     * GET /characters/{id}/tp-history
     * Obter histórico de transações de TP do personagem
     * Mostra todas as transações (ganhos e gastos) ordenadas por data decrescente
     */
    @GetMapping("/{id}/tp-history")
    public ResponseEntity<List<TPTransaction>> getTPHistory(@PathVariable UUID id) {
        log.info("Getting TP history for character {}", id);

        // Validar que o personagem existe
        if (!characterService.characterExists(id)) {
            return ResponseEntity.notFound().build();
        }

        List<TPTransaction> history = tpService.getTransactionHistory(id);
        return ResponseEntity.ok(history);
    }


    // ==================== 5️⃣ EXPERIÊNCIA E LEVELING ====================

    /**
     * POST /characters/{characterId}/gain-exp
     * Ganhar experiência (calcula level up automático)
     */
    @PostMapping("/{characterId}/gain-exp")
    public ResponseEntity<Character> gainExperience(
            @PathVariable UUID characterId,
            @Valid @RequestBody GainExpRequest request) {
        log.info("Character {} gaining {} experience for reason: {}",
                characterId, request.getAmount(), request.getReason());
        Character updated = experienceService.gainExperience(characterId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /characters/{characterId}/level-progress
     * Obter progresso de XP para o próximo level
     */
    @GetMapping("/{characterId}/level-progress")
    public ResponseEntity<ExperienceService.LevelProgress> getLevelProgress(@PathVariable UUID characterId) {
        log.info("Getting level progress for character {}", characterId);
        ExperienceService.LevelProgress progress = experienceService.getLevelProgress(characterId);
        return ResponseEntity.ok(progress);
    }

    /**
     * GET /characters/experience/exp-table
     * Tabela de experiência (quantos XP por level)
     */
    @GetMapping("/experience/exp-table")
    public ResponseEntity<List<ExperienceTable>> getExperienceTable(
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "20") int end) {

        if (start < 1 || end > 100 || start > end) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Getting experience table from level {} to {}", start, end);

        List<ExperienceTable> table = new ArrayList<>();

        for (int level = start; level <= end; level++) {
            long expRequired = experienceService.getExpRequiredForLevel(level);
            long totalExp = experienceService.getTotalExpForLevel(level);
            table.add(new ExperienceTable((int)level, expRequired, totalExp));
        }

        return ResponseEntity.ok(table);
    }

    /**
     * GET /characters/{characterId}/exp-info
     * Informações completas de experiência do personagem
     */
    @GetMapping("/{characterId}/exp-info")
    public ResponseEntity<ExperienceInfo> getExperienceInfo(@PathVariable UUID characterId) {
        log.info("Getting experience info for character {}", characterId);

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
     * PATCH /characters/{characterId}/set-level/{level}
     * [ADMIN] Definir level do personagem (reset XP)
     */
    @PatchMapping("/{characterId}/set-level/{level}")
    public ResponseEntity<Character> setCharacterLevel(
            @PathVariable UUID characterId,
            @PathVariable int level) {

        log.warn("ADMIN ACTION: Setting character {} level to {}", characterId, level);
        Character updated = experienceService.setLevel(characterId, level);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /characters/{characterId}/reset-experience
     * [ADMIN] Reset ar XP e Level para 1
     */
    @PatchMapping("/{characterId}/reset-experience")
    public ResponseEntity<Character> resetExperience(@PathVariable UUID characterId) {
        log.warn("ADMIN ACTION: Resetting experience for character {}", characterId);
        Character updated = experienceService.resetExperience(characterId);
        return ResponseEntity.ok(updated);
    }
}






