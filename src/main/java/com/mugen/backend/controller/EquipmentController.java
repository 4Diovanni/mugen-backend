package com.mugen.backend.controller;

import com.mugen.backend.dto.equipment.EquipItemRequest;
import com.mugen.backend.dto.equipment.EquipmentBonusesDTO;
import com.mugen.backend.dto.equipment.EquipmentStatsDTO;
import com.mugen.backend.entity.character.CharacterEquipment;
import com.mugen.backend.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * EquipmentController - Controller REST para gerenciar equipamento dos personagens
 * Endpoints para equipar/desequipar armas e armaduras
 * Endpoints:
 * 1. GET    /characters/{characterId}/equipment           - Obter equipamento atual
 * 2. POST   /characters/{characterId}/equipment/equip     - Equipar item (arma/armadura)
 * 3. POST   /characters/{characterId}/equipment/unequip   - Desequipar item
 * 4. GET    /characters/{characterId}/equipment/stats     - Calcular stats finais
 * 5. GET    /characters/{characterId}/equipment/bonuses   - Listar bônus do equipamento
 */
@Slf4j
@RestController
@RequestMapping("/characters/{characterId}/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * GET /characters/{characterId}/equipment
     * Obter equipamento atual do personagem
     */
    @GetMapping
    public ResponseEntity<CharacterEquipment> getEquipment(
            @PathVariable UUID characterId
    ) {
        log.info("[EQUIPMENT] GET - Obtendo equipamento do personagem: {}", characterId);
        CharacterEquipment equipment = equipmentService.getCharacterEquipment(characterId);
        return ResponseEntity.ok(equipment);
    }

    /**
     * POST /characters/{characterId}/equipment/equip
     * Equipar um item (arma ou armadura) no personagem
     */
    @PostMapping("/equip")
    public ResponseEntity<EquipmentStatsDTO> equipItem(
            @PathVariable UUID characterId,
            @Valid @RequestBody EquipItemRequest request
    ) {
        log.info("[EQUIPMENT] POST EQUIP - Personagem {} equipando item tipo: {}",
                characterId, request.getItemType());
        EquipmentStatsDTO stats = equipmentService.equipItem(characterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(stats);
    }

    /**
     * POST /characters/{characterId}/equipment/equip/weapon
     * Equipar arma especificamente
     */
    @PostMapping("/equip/weapon")
    public ResponseEntity<EquipmentStatsDTO> equipWeapon(
            @PathVariable UUID characterId,
            @Valid @RequestBody EquipItemRequest request
    ) {
        log.info("[EQUIPMENT] POST EQUIP WEAPON - Personagem {} equipando arma", characterId);
        EquipmentStatsDTO stats = equipmentService.equipWeapon(characterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(stats);
    }

    /**
     * POST /characters/{characterId}/equipment/equip/armor
     * Equipar armadura especificamente
     */
    @PostMapping("/equip/armor")
    public ResponseEntity<EquipmentStatsDTO> equipArmor(
            @PathVariable UUID characterId,
            @Valid @RequestBody EquipItemRequest request
    ) {
        log.info("[EQUIPMENT] POST EQUIP ARMOR - Personagem {} equipando armadura", characterId);
        EquipmentStatsDTO stats = equipmentService.equipArmor(characterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(stats);
    }

    /**
     * POST /characters/{characterId}/equipment/unequip
     * Desequipar um item (arma ou armadura) do personagem
     * Query: ?itemType=WEAPON ou ?itemType=ARMOR
     */
    @PostMapping("/unequip")
    public ResponseEntity<EquipmentStatsDTO> unequipItem(
            @PathVariable UUID characterId,
            @RequestParam String itemType
    ) {
        log.info("[EQUIPMENT] POST UNEQUIP - Personagem {} desequipando item tipo: {}",
                characterId, itemType);
        EquipmentStatsDTO stats = equipmentService.unequipItem(characterId, itemType);
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /characters/{characterId}/equipment/unequip/weapon
     * Desequipar arma especificamente
     */
    @PostMapping("/unequip/weapon")
    public ResponseEntity<EquipmentStatsDTO> unequipWeapon(
            @PathVariable UUID characterId
    ) {
        log.info("[EQUIPMENT] POST UNEQUIP WEAPON - Personagem {} desequipando arma", characterId);
        EquipmentStatsDTO stats = equipmentService.unequipWeapon(characterId);
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /characters/{characterId}/equipment/unequip/armor
     * Desequipar armadura especificamente
     */
    @PostMapping("/unequip/armor")
    public ResponseEntity<EquipmentStatsDTO> unequipArmor(
            @PathVariable UUID characterId
    ) {
        log.info("[EQUIPMENT] POST UNEQUIP ARMOR - Personagem {} desequipando armadura", characterId);
        EquipmentStatsDTO stats = equipmentService.unequipArmor(characterId);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /characters/{characterId}/equipment/stats
     * Calcular stats finais do personagem considerando equipamento ativo
     */
    @GetMapping("/stats")
    public ResponseEntity<EquipmentStatsDTO> getEquipmentStats(
            @PathVariable UUID characterId
    ) {
        log.info("[EQUIPMENT] GET STATS - Calculando stats de equipamento para: {}", characterId);
        EquipmentStatsDTO stats = equipmentService.getEquipmentStats(characterId);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /characters/{characterId}/equipment/bonuses
     * Obter breakdown detalhado de todos os bônus do equipamento
     */
    @GetMapping("/bonuses")
    public ResponseEntity<EquipmentBonusesDTO> getEquipmentBonuses(
            @PathVariable UUID characterId
    ) {
        log.info("[EQUIPMENT] GET BONUSES - Obtendo bônus de equipamento para: {}", characterId);
        EquipmentBonusesDTO bonuses = equipmentService.getEquipmentBonuses(characterId);
        return ResponseEntity.ok(bonuses);
    }

    /**
     * GET /characters/{characterId}/equipment/health
     * Health check do Equipment API
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(
            @PathVariable UUID characterId
    ) {
        log.debug("[EQUIPMENT] Health check para personagem: {}", characterId);
        return ResponseEntity.ok("Equipment API OK para personagem: " + characterId);
    }
}