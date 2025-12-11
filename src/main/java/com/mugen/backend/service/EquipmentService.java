package com.mugen.backend.service;

import com.mugen.backend.entity.character.CharacterEquipment;
import com.mugen.backend.repository.CharacterEquipmentRepository;
import com.mugen.backend.repository.inventory.InventoryArmorRepository;
import com.mugen.backend.repository.inventory.InventoryWeaponRepository;
import com.mugen.backend.dto.equipment.*;
import com.mugen.backend.dto.inventory.WeaponDTO;
import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.inventory.*;
import com.mugen.backend.enums.EquipmentStatus;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.exception.ResourceNotFoundException;
import com.mugen.backend.exception.InvalidOperationException;
import com.mugen.backend.service.inventory.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EquipmentService {

    private final CharacterEquipmentRepository equipmentRepository;
    private final CharacterRepository characterRepository;
    private final InventoryWeaponRepository inventoryWeaponRepository;
    private final InventoryArmorRepository inventoryArmorRepository;
    private final WeaponService weaponService;
    private final ArmorService armorService;
    private final CharacterAttributesCalculatorService attributesCalculator;

    // ==================== GET EQUIPMENT ====================

    /**
     * ✅ NOVO: Obter equipamento do personagem
     */
    public CharacterEquipment getCharacterEquipment(UUID characterId) {
        log.debug("Buscando equipamento do personagem: {}", characterId);
        return equipmentRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new InvalidOperationException("Personagem não possui equipamento"));
    }

    /**
     * ✅ MÉTODO AUXILIAR: Calcular o status correto baseado no que está equipado
     */
    private EquipmentStatus calculateStatus(Weapon weapon, Armor armor) {
        boolean hasWeapon = weapon != null;
        boolean hasArmor = armor != null;

        if (hasWeapon && hasArmor) {
            return EquipmentStatus.EQUIPADO_AA;  // Ambos equipados
        } else if (hasWeapon) {
            return EquipmentStatus.EQUIPADO_ARMA;  // Apenas arma
        } else if (hasArmor) {
            return EquipmentStatus.EQUIPADO_ARMADURA;  // Apenas armadura
        } else {
            return EquipmentStatus.NAO_EQUIPADO;  // Nenhum equipado
        }
    }

    /**
     * ✅ EXISTENTE: Obter stats de equipamento (CORRIGIDO)
     */
    public EquipmentStatsDTO getEquipmentStats(UUID characterId) {
        log.debug("🔍 Buscando stats de equipamento do personagem: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Personagem não encontrado"));

        if (character.getAttributes() == null) {
            log.error("❌ Personagem {} sem atributos!", characterId);
            throw new InvalidOperationException("Personagem não possui atributos definidos");
        }

        CharacterEquipment equipment = equipmentRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new InvalidOperationException("❌ Personagem não possui equipamento"));

        WeaponDTO weaponDTO = null;
        ArmorDTO armorDTO = null;

        if (equipment.getWeapon() != null) {
            weaponDTO = weaponService.convertToDTO(equipment.getWeapon());
        }

        if (equipment.getArmor() != null) {
            armorDTO = armorService.convertToDTO(equipment.getArmor());
        }

        // ✅ NOVO: Calcular atributos finais (base + bônus)
        int finalStr = character.getAttributes().getStr() + equipment.getTotalStrBonus();
        int finalDex = character.getAttributes().getDex() + equipment.getTotalDexBonus();
        int finalCon = character.getAttributes().getCon() + equipment.getTotalConBonus();
        int finalWil = character.getAttributes().getWil() + equipment.getTotalWilBonus();
        int finalMnd = character.getAttributes().getMnd() + equipment.getTotalMndBonus();
        int finalSpi = character.getAttributes().getSpi() + equipment.getTotalSpiBonus();

        String equipmentSummary = buildEquipmentSummary(weaponDTO, armorDTO);
        String bonusSummary = buildBonusSummary(equipment);
        String attributesSummary = buildAttributesSummary(finalStr, finalDex, finalCon, finalWil, finalMnd, finalSpi);

        return EquipmentStatsDTO.builder()
                .characterId(characterId)
                .equipmentId(equipment.getId())
                .status(equipment.getStatus())
                .hasWeapon(weaponDTO != null)
                .hasArmor(armorDTO != null)
                .fullyEquipped(weaponDTO != null && armorDTO != null)
                .weapon(weaponDTO)
                .armor(armorDTO)
                .weaponName(weaponDTO != null ? weaponDTO.getName() : null)
                .weaponId(weaponDTO != null ? weaponDTO.getId() : null)
                .armorName(armorDTO != null ? armorDTO.getName() : null)
                .armorId(armorDTO != null ? armorDTO.getId() : null)
                .totalStrBonus(equipment.getTotalStrBonus())
                .totalDexBonus(equipment.getTotalDexBonus())
                .totalConBonus(equipment.getTotalConBonus())
                .totalWilBonus(equipment.getTotalWilBonus())
                .totalMndBonus(equipment.getTotalMndBonus())
                .totalSpiBonus(equipment.getTotalSpiBonus())
                // ✅ NOVO: Atributos finais (base + bônus)
                .finalStr(finalStr)
                .finalDex(finalDex)
                .finalCon(finalCon)
                .finalWil(finalWil)
                .finalMnd(finalMnd)
                .finalSpi(finalSpi)
                .weaponEquippedAt(equipment.getEquippedAt())
                .armorEquippedAt(equipment.getEquippedAt())
                .equipmentSummary(equipmentSummary)
                .bonusSummary(bonusSummary)
                .attributesSummary(attributesSummary)
                .build();
    }

    // ==================== EQUIPMENT BONUSES ====================

    /**
     * ✅ NOVO: Calcular bônus totais do equipamento
     */
    public EquipmentBonusesDTO calculateEquipmentBonuses(WeaponDTO weapon, ArmorDTO armor) {
        log.debug("Calculando bônus totais de equipamento");

        int totalStrBonus = 0;
        int totalDexBonus = 0;
        int totalConBonus = 0;
        int totalWilBonus = 0;
        int totalMndBonus = 0;
        int totalSpiBonus = 0;

        if (weapon != null) {
            totalStrBonus += weapon.getStrBonus() != null ? weapon.getStrBonus() : 0;
            totalDexBonus += weapon.getDexBonus() != null ? weapon.getDexBonus() : 0;
            totalConBonus += weapon.getConBonus() != null ? weapon.getConBonus() : 0;
            totalWilBonus += weapon.getWilBonus() != null ? weapon.getWilBonus() : 0;
            totalMndBonus += weapon.getMndBonus() != null ? weapon.getMndBonus() : 0;
            totalSpiBonus += weapon.getSpiBonus() != null ? weapon.getSpiBonus() : 0;
        }

        if (armor != null) {
            totalStrBonus += armor.getStrBonus() != null ? armor.getStrBonus() : 0;
            totalDexBonus += armor.getDexBonus() != null ? armor.getDexBonus() : 0;
            totalConBonus += armor.getConBonus() != null ? armor.getConBonus() : 0;
        }

        return EquipmentBonusesDTO.builder()
                .strBonus(totalStrBonus)
                .dexBonus(totalDexBonus)
                .conBonus(totalConBonus)
                .wilBonus(totalWilBonus)
                .mndBonus(totalMndBonus)
                .spiBonus(totalSpiBonus)
                .build();
    }

    /**
     * ✅ NOVO: Obter apenas os bônus de equipamento
     */
    public EquipmentBonusesDTO getEquipmentBonuses(UUID characterId) {
        log.debug("Obtendo bônus de equipamento do personagem: {}", characterId);

        CharacterEquipment equipment = getCharacterEquipment(characterId);

        WeaponDTO weaponDTO = null;
        ArmorDTO armorDTO = null;

        if (equipment.getWeapon() != null) {
            weaponDTO = weaponService.convertToDTO(equipment.getWeapon());
        }

        if (equipment.getArmor() != null) {
            armorDTO = armorService.convertToDTO(equipment.getArmor());
        }

        return calculateEquipmentBonuses(weaponDTO, armorDTO);
    }

    // ==================== EQUIP ITEMS ====================

    /**
     * ✅ NOVO: Equipar item (genérico)
     */
    @Transactional
    public EquipmentStatsDTO equipItem(UUID characterId, EquipItemRequest request) {
        log.info("⚙️ Equipando item para personagem: {}", characterId);

        if ("WEAPON".equalsIgnoreCase(request.getItemType())) {
            return equipWeapon(characterId, request);
        } else if ("ARMOR".equalsIgnoreCase(request.getItemType())) {
            return equipArmor(characterId, request);
        } else {
            throw new InvalidOperationException("Tipo de item inválido: " + request.getItemType());
        }
    }

    /**
     * ✅ EXISTENTE: Equipar arma (CORRIGIDO)
     */
    @Transactional
    public EquipmentStatsDTO equipWeapon(UUID characterId, EquipItemRequest request) {
        log.info("⚙️ Equipando arma para personagem: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Personagem não encontrado"));

        if (character.getAttributes() == null) {
            throw new InvalidOperationException("Personagem sem atributos definidos");
        }

        CharacterEquipment equipment = equipmentRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new InvalidOperationException("Personagem não possui equipamento"));

        InventoryWeapon inventoryWeapon = inventoryWeaponRepository
                .findById(request.getInventoryItemId())
                .orElseThrow(() -> new ResourceNotFoundException("❌ Arma não encontrada no inventário"));

        WeaponDTO weaponDTO = weaponService.convertToDTO(inventoryWeapon.getWeapon());
        boolean canEquip = weaponService.canEquipWeapon(
                weaponDTO,
                character.getAttributes().getStr(),
                character.getAttributes().getDex(),
                character.getAttributes().getCon(),
                character.getAttributes().getWil(),
                character.getAttributes().getMnd(),
                character.getAttributes().getSpi(),
                character.getLevel()
        );

        if (!canEquip) {
            log.warn("❌ Personagem não atende requisitos para arma: {}", weaponDTO.getName());
            throw new InvalidOperationException(
                    String.format("❌ Personagem não atende aos requisitos para equipar %s", weaponDTO.getName())
            );
        }

        // ✅ CORRIGIDO: Equipar arma e RECALCULAR o status baseado no que está equipado
        equipment.setWeapon(inventoryWeapon.getWeapon());
        equipment.setEquippedAt(LocalDateTime.now());

        // ✅ NOVO: Calcular status correto (pode ser EQUIPADO_ARMA ou EQUIPADO_AA)
        EquipmentStatus newStatus = calculateStatus(equipment.getWeapon(), equipment.getArmor());
        equipment.setStatus(newStatus);

        equipmentRepository.save(equipment);

        log.info("✅ Arma {} equipada com sucesso. Status: {}", weaponDTO.getName(), newStatus);
        return getEquipmentStats(characterId);
    }

    /**
     * ✅ NOVO: Equipar armadura (CORRIGIDO)
     */
    @Transactional
    public EquipmentStatsDTO equipArmor(UUID characterId, EquipItemRequest request) {
        log.info("⚙️ Equipando armadura para personagem: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Personagem não encontrado"));

        if (character.getAttributes() == null) {
            throw new InvalidOperationException("Personagem sem atributos definidos");
        }

        CharacterEquipment equipment = equipmentRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new InvalidOperationException("Personagem não possui equipamento"));

        InventoryArmor inventoryArmor = inventoryArmorRepository
                .findById(request.getInventoryItemId())
                .orElseThrow(() -> new ResourceNotFoundException("❌ Armadura não encontrada no inventário"));

        ArmorDTO armorDTO = armorService.convertToDTO(inventoryArmor.getArmor());
        boolean canEquip = armorService.canEquipArmor(
                armorDTO,
                character.getAttributes().getStr(),
                character.getLevel()
        );

        if (!canEquip) {
            log.warn("❌ Personagem não atende requisitos para armadura: {}", armorDTO.getName());
            throw new InvalidOperationException(
                    String.format("❌ Personagem não atende aos requisitos para equipar %s", armorDTO.getName())
            );
        }

        // ✅ CORRIGIDO: Equipar armadura e RECALCULAR o status baseado no que está equipado
        equipment.setArmor(inventoryArmor.getArmor());
        equipment.setEquippedAt(LocalDateTime.now());

        // ✅ NOVO: Calcular status correto (pode ser EQUIPADO_ARMADURA ou EQUIPADO_AA)
        EquipmentStatus newStatus = calculateStatus(equipment.getWeapon(), equipment.getArmor());
        equipment.setStatus(newStatus);

        equipmentRepository.save(equipment);

        log.info("✅ Armadura {} equipada com sucesso. Status: {}", armorDTO.getName(), newStatus);
        return getEquipmentStats(characterId);
    }

    // ==================== UNEQUIP ITEMS ====================

    /**
     * ✅ NOVO: Desequipar arma (CORRIGIDO)
     */
    public EquipmentStatsDTO unequipWeapon(UUID characterId) {
        log.info("🗑️ Desequipando arma do personagem: {}", characterId);

        CharacterEquipment equipment = getCharacterEquipment(characterId);

        if (equipment.getWeapon() == null) {
            throw new InvalidOperationException("Personagem não possui arma equipada");
        }

        // ✅ CORRIGIDO: Remover arma e RECALCULAR status
        equipment.setWeapon(null);
        equipment.setEquippedAt(null);

        // ✅ NOVO: Calcular status correto (pode virar EQUIPADO_ARMADURA ou NAO_EQUIPADO)
        EquipmentStatus newStatus = calculateStatus(equipment.getWeapon(), equipment.getArmor());
        equipment.setStatus(newStatus);

        equipmentRepository.save(equipment);

        log.info("✅ Arma desequipada com sucesso. Status: {}", newStatus);
        return getEquipmentStats(characterId);
    }

    /**
     * ✅ NOVO: Desequipar armadura (CORRIGIDO)
     */
    public EquipmentStatsDTO unequipArmor(UUID characterId) {
        log.info("🗑️ Desequipando armadura do personagem: {}", characterId);

        CharacterEquipment equipment = getCharacterEquipment(characterId);

        if (equipment.getArmor() == null) {
            throw new InvalidOperationException("Personagem não possui armadura equipada");
        }

        // ✅ CORRIGIDO: Remover armadura e RECALCULAR status
        equipment.setArmor(null);
        equipment.setEquippedAt(null);

        // ✅ NOVO: Calcular status correto (pode virar EQUIPADO_ARMA ou NAO_EQUIPADO)
        EquipmentStatus newStatus = calculateStatus(equipment.getWeapon(), equipment.getArmor());
        equipment.setStatus(newStatus);

        equipmentRepository.save(equipment);

        log.info("✅ Armadura desequipada com sucesso. Status: {}", newStatus);
        return getEquipmentStats(characterId);
    }

    /**
     * ✅ NOVO: Desequipar item (genérico)
     */
    public EquipmentStatsDTO unequipItem(UUID characterId, String itemType) {
        log.info("🗑️ Desequipando item {} do personagem: {}", itemType, characterId);

        if ("WEAPON".equalsIgnoreCase(itemType)) {
            return unequipWeapon(characterId);
        } else if ("ARMOR".equalsIgnoreCase(itemType)) {
            return unequipArmor(characterId);
        } else {
            throw new InvalidOperationException("Tipo de item inválido: " + itemType);
        }
    }

    // ==================== CALCULATE STATS ====================

    /**
     * ✅ NOVO: Calcular stats finais do personagem com equipamento
     */
    public EquipmentStatsDTO calculateEquipmentStats(UUID characterId) {
        log.debug("Calculando stats de equipamento do personagem: {}", characterId);
        return getEquipmentStats(characterId);
    }

    // ==================== HELPER METHODS ====================

    private String buildEquipmentSummary(WeaponDTO weapon, ArmorDTO armor) {
        StringBuilder sb = new StringBuilder();
        if (weapon != null) {
            sb.append("Arma: ").append(weapon.getDisplayName());
        } else {
            sb.append("Arma: Nenhuma");
        }

        sb.append(" | ");
        if (armor != null) {
            sb.append("Armadura: ").append(armor.getDisplayName());
        } else {
            sb.append("Armadura: Nenhuma");
        }

        return sb.toString();
    }
    private String buildBonusSummary(CharacterEquipment equipment) {
        return String.format(
                "STR: +%d | DEX: +%d | CON: +%d | WIL: +%d | MND: +%d | SPI: +%d",
                equipment.getTotalStrBonus(),
                equipment.getTotalDexBonus(),
                equipment.getTotalConBonus(),
                equipment.getTotalWilBonus(),
                equipment.getTotalMndBonus(),
                equipment.getTotalSpiBonus()
        );
    }

    private String buildAttributesSummary(int str, int dex, int con, int wil, int mnd, int spi) {
        return String.format(
                "STR: %d | DEX: %d | CON: %d | WIL: %d | MND: %d | SPI: %d",
                str, dex, con, wil, mnd, spi
        );
    }
}
