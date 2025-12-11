package com.mugen.backend.controller;

import com.mugen.backend.dto.equipment.EquipItemRequest;
import com.mugen.backend.dto.equipment.EquipmentBonusesDTO;
import com.mugen.backend.dto.equipment.EquipmentStatsDTO;
import com.mugen.backend.dto.inventory.*;
import com.mugen.backend.entity.inventory.Inventory;
import com.mugen.backend.entity.character.CharacterEquipment;
import com.mugen.backend.service.inventory.InventoryService;
import com.mugen.backend.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Inventory & Equipment Controller
 * API para gerir inventário e equipamento dos personagens

 * Organização:
 * 1. 📦 INVENTÁRIO (Listar, Buscar, Info)
 * 2. ⚔️ ARMAS (Comprar, Vender, Listar)
 * 3. 🛡️ ARMADURAS (Comprar, Vender, Listar)
 * 4. 🧱 MATERIAIS (Comprar, Vender, Listar)
 * 5. ⚙️ EQUIPAMENTO (Equipar, Desequipar, Stats)
 */
@RestController
@RequestMapping("/characters/{characterId}/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;
    private final EquipmentService equipmentService;

    // ==================== 1️⃣ INVENTÁRIO ====================

    /**
     * GET /characters/{characterId}/inventory
     * Obter inventário completo do personagem
     */
    @GetMapping
    public ResponseEntity<InventoryInfoDTO> getInventory(@PathVariable UUID characterId) {
        log.info("Getting inventory for character {}", characterId);
        InventoryInfoDTO inventory = inventoryService.getInventoryInfo(characterId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * GET /characters/{{characterId}}/inventory/info
     * Informações detalhadas do inventário (slots, limites, etc)
     */
    @GetMapping("/info")
    public ResponseEntity<InventoryInfoDTO> getInventoryInfo(@PathVariable UUID characterId) {
        log.info("Getting detailed inventory info for character {}", characterId);
        InventoryInfoDTO info = inventoryService.getInventoryInfo(characterId);
        return ResponseEntity.ok(info);
    }

    /**
     * GET /characters/{characterId}/inventory/weight
     * Verificar peso/slots do inventário
     */
    @GetMapping("/weight")
    public ResponseEntity<InventoryWeightDTO> getInventoryWeight(@PathVariable UUID characterId) {
        log.info("Getting inventory weight for character {}", characterId);
        Inventory inventory = inventoryService.getInventory(characterId);
        
        InventoryWeightDTO weight = InventoryWeightDTO.builder()
                .totalWeapons(inventoryService.getTotalWeapons(characterId))
                .totalArmors(inventoryService.getTotalArmors(characterId))
                .totalMaterials(inventoryService.getTotalMaterials(characterId))
                .build();
        
        return ResponseEntity.ok(weight);
    }

    // ==================== 2️⃣ ARMAS ====================

    /**
     * GET /characters/{characterId}/inventory/weapons
     * Listar todas as armas do personagem
     */
    @GetMapping("/weapons")
    public ResponseEntity<List<InventoryItemDTO>> getWeapons(@PathVariable UUID characterId) {
        log.info("Getting weapons for character {}", characterId);
        List<InventoryItemDTO> weapons = inventoryService.getCharacterWeapons(characterId);
        return ResponseEntity.ok(weapons);
    }

    /**
     * GET /characters/{characterId}/inventory/weapons/{weaponId}
     * Buscar arma específica no inventário
     */
    @GetMapping("/weapons/{weaponId}")
    public ResponseEntity<InventoryItemDTO> getWeapon(
            @PathVariable UUID characterId,
            @PathVariable Integer weaponId) {
        log.info("Getting weapon {} for character {}", weaponId, characterId);
        InventoryItemDTO weapon = inventoryService.getWeaponFromInventory((long) weaponId);
        return ResponseEntity.ok(weapon);
    }

    /**
     * POST /characters/{{characterId}}/inventory/weapons/buy
     * Comprar arma
     */
    @PostMapping("/weapons/buy")
    public ResponseEntity<BuyItemResponse> buyWeapon(
            @PathVariable UUID characterId,
            @Valid @RequestBody BuyWeaponRequest request) {
        log.info("Character {} buying weapon {}", characterId, request.getWeaponId());
        BuyItemResponse response = inventoryService.buyWeapon(characterId, request.getWeaponId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /characters/{{characterId}}/inventory/weapons/sell
     * Vender arma
     */
    @PostMapping("/weapons/sell")
    public ResponseEntity<SellItemResponse> sellWeapon(
            @PathVariable UUID characterId,
            @Valid @RequestBody SellItemRequest request) {
        log.info("Character {} selling weapon {} x{}", characterId, request.getItemId(), request.getQuantity());
        SellItemResponse response = inventoryService.sellWeapon(characterId, request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(response);
    }

    // ==================== 3️⃣ ARMADURAS ====================

    /**
     * GET /characters/{{characterId}}/inventory/armors
     * Listar todas as armaduras do personagem
     */
    @GetMapping("/armors")
    public ResponseEntity<List<InventoryItemDTO>> getArmors(@PathVariable UUID characterId) {
        log.info("Getting armors for character {}", characterId);
        List<InventoryItemDTO> armors = inventoryService.getCharacterArmors(characterId);
        return ResponseEntity.ok(armors);
    }

    /**
     * GET /characters/{{characterId}}/inventory/armors/{armorId}
     * Buscar armadura específica no inventário
     */
    @GetMapping("/armors/{armorId}")
    public ResponseEntity<InventoryItemDTO> getArmor(
            @PathVariable UUID characterId,
            @PathVariable Integer armorId) {
        log.info("Getting armor {} for character {}", armorId, characterId);
        InventoryItemDTO armor = inventoryService.getArmorFromInventory((long) armorId);
        return ResponseEntity.ok(armor);
    }


    /**
     * POST /characters/{{characterId}}/inventory/weapons/buy
     * Comprar arma
     */
    @PostMapping("/armor/buy")
    public ResponseEntity<BuyItemResponse> buyArmor(
            @PathVariable UUID characterId,
            @Valid @RequestBody BuyArmorRequest request) {
        log.info("Character {} buying weapon {}", characterId, request.getArmorId());
        BuyItemResponse response = inventoryService.buyArmor(characterId, request.getArmorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /characters/{{characterId}}/inventory/armors/sell
     * Vender armadura
     */
    @PostMapping("/armors/sell")
    public ResponseEntity<SellItemResponse> sellArmor(
            @PathVariable UUID characterId,
            @Valid @RequestBody SellItemRequest request) {
        log.info("Character {} selling armor {} x{}", characterId, request.getItemId(), request.getQuantity());
        SellItemResponse response = inventoryService.sellArmor(characterId, request.getItemId(), request.getQuantity());
        return ResponseEntity.ok(response);
    }

    // ==================== 4️⃣ MATERIAIS ====================

    /**
     * GET /characters/{{characterId}}/inventory/materials
     * Listar todos os materiais do personagem
     */
    @GetMapping("/materials")
    public ResponseEntity<List<InventoryItemDTO>> getMaterials(@PathVariable UUID characterId) {
        log.info("Getting materials for character {}", characterId);
        List<InventoryItemDTO> materials = inventoryService.getCharacterMaterials(characterId);
        return ResponseEntity.ok(materials);
    }

    /**
     * GET /characters/{{characterId}}/inventory/materials/{materialId}
     * Buscar material específico no inventário
     */
    @GetMapping("/materials/{materialId}")
    public ResponseEntity<InventoryItemDTO> getMaterial(
            @PathVariable UUID characterId,
            @PathVariable Integer materialId) {
        log.info("Getting material {} for character {}", materialId, characterId);
        InventoryItemDTO material = inventoryService.getMaterialFromInventory(materialId);
        return ResponseEntity.ok(material);
    }

    // ==================== 5️⃣ EQUIPAMENTO ====================

    /**
     * GET /characters/{{characterId}}/equipment
     * Obter equipamento atual do personagem
     */
    @GetMapping("/equipment")
    public ResponseEntity<CharacterEquipment> getEquipment(@PathVariable UUID characterId) {
        log.info("Getting equipment for character {}", characterId);
        CharacterEquipment equipment = equipmentService.getCharacterEquipment(characterId);
        return ResponseEntity.ok(equipment);
    }

    /**
     * POST /characters/{{characterId}}/equipment/equip
     * Equipar item (arma ou armadura)
     */
    @PostMapping("/equipment/equip")
    public ResponseEntity<EquipmentStatsDTO> equipItem(
            @PathVariable UUID characterId,
            @Valid @RequestBody EquipItemRequest request) {
        log.info("Character {} equipping {} {}", characterId, request.getItemType(), request.getItemId());
        EquipmentStatsDTO equipment = equipmentService.equipItem(characterId, request);
        return ResponseEntity.ok(equipment);
    }

    /**
     * POST /characters/{{characterId}}/equipment/unequip
     * Desequipar item
     */
    @PostMapping("/equipment/unequip")
    public ResponseEntity<EquipmentStatsDTO> unequipItem(
            @PathVariable UUID characterId,
            @RequestParam String itemType) {
        log.info("Character {} unequipping {}", characterId, itemType);
        EquipmentStatsDTO equipment = equipmentService.unequipItem(characterId, itemType);
        return ResponseEntity.ok(equipment);
    }

    /**
     * GET /characters/{{characterId}}/equipment/stats
     * Calcular stats finais com equipamento
     */
    @GetMapping("/equipment/stats")
    public ResponseEntity<EquipmentStatsDTO> getEquipmentStats(@PathVariable UUID characterId) {
        log.info("Calculating equipment stats for character {}", characterId);
        EquipmentStatsDTO stats = equipmentService.calculateEquipmentStats(characterId);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /characters/{{characterId}}/equipment/bonuses
     * Listar todos os bônus do equipamento atual
     */
    @GetMapping("/equipment/bonuses")
    public ResponseEntity<EquipmentBonusesDTO> getEquipmentBonuses(@PathVariable UUID characterId) {
        log.info("Getting equipment bonuses for character {}", characterId);
        EquipmentBonusesDTO bonuses = equipmentService.getEquipmentBonuses(characterId);
        return ResponseEntity.ok(bonuses);
    }
}
