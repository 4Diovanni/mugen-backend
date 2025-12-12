package com.mugen.backend.service.inventory;

import com.mugen.backend.dto.inventory.*;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.inventory.*;
import com.mugen.backend.exception.InsufficientTPException;
import com.mugen.backend.exception.InvalidOperationException;
import com.mugen.backend.exception.ResourceNotFoundException;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.inventory.*;
import com.mugen.backend.service.TPService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service para gerir Inventários
 * Responsável por:
 * - Gerir itens no inventário
 * - Comprar e vender itens
 * - Validar espaço disponível
 * - Calcular preços (venda = 50% da compra)
 * - Integração com TP System
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    // ==================== REPOSITORIES ====================
    // Para gerenciar INVENTORY ITEMS (Weapon, Armor, Material no inventário)
    private final InventoryRepository inventoryRepository;
    private final InventoryWeaponRepository inventoryWeaponRepository;
    private final InventoryArmorRepository inventoryArmorRepository;
    private final InventoryMaterialRepository inventoryMaterialRepository;

    // Para buscar as ENTITIES (Weapon, Armor da base de dados)
    private final WeaponRepository weaponRepository;
    private final ArmorRepository armorRepository;
    @Getter
    private final MaterialRepository materialRepository;

    private final CharacterRepository characterRepository;
    private final WeaponService weaponService;
    private final ArmorService armorService;
    @Getter
    private final TPService tpService;

    private static final int SLOT_USAGE_WEAPON = 1;
    private static final int SLOT_USAGE_ARMOR = 1;
    private static final int SLOT_USAGE_MATERIAL = 1;

    // ==================== GET INVENTORY ====================

    /**
     * ✅ NOVO: Obter inventário do personagem
     */
    public Inventory getInventory(UUID characterId) {
        log.debug("Buscando inventário do personagem: {}", characterId);
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));

        Inventory inventory = character.getInventory();
        if (inventory == null) {
            throw new InvalidOperationException("Personagem não possui inventário");
        }
        return inventory;
    }

    /**
     * ✅ CORRIGIDO: getInventoryInfo()
     * - Usando findByInventoryId() em vez de findAll()
     * - Removido getTpCost() de Material (não existe)
     */
    public InventoryInfoDTO getInventoryInfo(UUID characterId) {
        log.debug("Buscando inventário do personagem: {}", characterId);
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Personagem não encontrado: " + characterId
                ));

        Inventory inventory = character.getInventory();
        if (inventory == null) {
            throw new InvalidOperationException("Personagem não possui inventário");
        }

        // ✅ CORRETO: Usar inventoryWeaponRepository para buscar items do inventário
        List<InventoryWeapon> weapons = inventoryWeaponRepository.findByInventoryId(inventory.getId());
        List<InventoryArmor> armors = inventoryArmorRepository.findByInventoryId(inventory.getId());
        List<InventoryMaterial> materials = inventoryMaterialRepository.findByInventoryId(inventory.getId());

        // Calcular slots usados
        int slotsUsed = (weapons.size() * SLOT_USAGE_WEAPON)
                + (armors.size() * SLOT_USAGE_ARMOR)
                + (materials.size() * SLOT_USAGE_MATERIAL);

        int freeSlots = inventory.getMaxSlots() - slotsUsed;
        int usagePercentage = slotsUsed > 0 ? (slotsUsed * 100) / inventory.getMaxSlots() : 0;

        // ✅ CORRIGIDO: Material NÃO tem getTpCost(), removido do cálculo
        Long totalValue = weapons.stream()
                .mapToLong(w -> (w.getWeapon().getTpCost() != null ? w.getWeapon().getTpCost() : 0) * w.getQuantity())
                .sum()
                + armors.stream()
                .mapToLong(a -> (a.getArmor().getTpCost() != null ? a.getArmor().getTpCost() : 0) * a.getQuantity())
                .sum();
        // Removido: materials não têm getTpCost()

        // Converter itens para DTOs
        List<InventoryItemDTO> weaponDTOs = weapons.stream()
                .map(this::convertWeaponToItemDTO)
                .toList();

        List<InventoryItemDTO> armorDTOs = armors.stream()
                .map(this::convertArmorToItemDTO)
                .toList();

        String statusMessage = String.format(
                "Inventário: %d/%d slots (%.0f%% usado)",
                slotsUsed, inventory.getMaxSlots(), (double) usagePercentage
        );

        return InventoryInfoDTO.builder()
                .inventoryId(inventory.getId())
                .characterId(characterId)
                .currentSlots(slotsUsed)
                .maxSlots(inventory.getMaxSlots())
                .freeSlots(freeSlots)
                .usagePercentage(usagePercentage)
                .isFull(freeSlots <= 0)
                .totalValue(totalValue)
                .weapons(weaponDTOs)
                .armors(armorDTOs)
                .totalWeapons(weapons.size())
                .totalArmors(armors.size())
                .totalMaterials(materials.size())
                .totalItems(weapons.size() + armors.size() + materials.size())
                .lastUpdated(LocalDateTime.now())
                .statusMessage(statusMessage)
                .build();
    }

    // ==================== GET TOTALS ====================

    /**
     * ✅ NOVO: Obter quantidade total de armas
     */
    public Integer getTotalWeapons(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryWeapon> weapons = inventoryWeaponRepository.findByInventoryId(inventory.getId());
        return weapons.stream().mapToInt(InventoryWeapon::getQuantity).sum();
    }

    /**
     * ✅ NOVO: Obter quantidade total de armaduras
     */
    public Integer getTotalArmors(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryArmor> armors = inventoryArmorRepository.findByInventoryId(inventory.getId());
        return armors.stream().mapToInt(InventoryArmor::getQuantity).sum();
    }

    /**
     * ✅ NOVO: Obter quantidade total de materiais
     */
    public Integer getTotalMaterials(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryMaterial> materials = inventoryMaterialRepository.findByInventoryId(inventory.getId());
        return materials.stream().mapToInt(InventoryMaterial::getQuantity).sum();
    }

    // ==================== GET CHARACTER ITEMS ====================

    /**
     * ✅ NOVO: Obter todas as armas do personagem
     */
    public List<InventoryItemDTO> getCharacterWeapons(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryWeapon> weapons = inventoryWeaponRepository.findByInventoryId(inventory.getId());
        return weapons.stream().map(this::convertWeaponToItemDTO).toList();
    }

    /**
     * ✅ NOVO: Obter todas as armaduras do personagem
     */
    public List<InventoryItemDTO> getCharacterArmors(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryArmor> armors = inventoryArmorRepository.findByInventoryId(inventory.getId());
        return armors.stream().map(this::convertArmorToItemDTO).toList();
    }

    /**
     * ✅ NOVO: Obter todos os materiais do personagem
     */
    public List<InventoryItemDTO> getCharacterMaterials(UUID characterId) {
        Inventory inventory = getInventory(characterId);
        List<InventoryMaterial> materials = inventoryMaterialRepository.findByInventoryId(inventory.getId());
        return materials.stream().map(this::convertMaterialToItemDTO).toList();
    }

    // ==================== GET FROM INVENTORY ====================

    /**
     * ✅ NOVO: Obter arma específica do inventário
     */
    public InventoryItemDTO getWeaponFromInventory(Long inventoryWeaponId) {
        InventoryWeapon weapon = inventoryWeaponRepository.findById(inventoryWeaponId)
                .orElseThrow(() -> new ResourceNotFoundException("Arma não encontrada no inventário"));
        return convertWeaponToItemDTO(weapon);
    }

    /**
     * ✅ NOVO: Obter armadura específica do inventário
     */
    public InventoryItemDTO getArmorFromInventory(Long inventoryArmorId) {
        InventoryArmor armor = inventoryArmorRepository.findById(inventoryArmorId)
                .orElseThrow(() -> new ResourceNotFoundException("Armadura não encontrada no inventário"));
        return convertArmorToItemDTO(armor);
    }

    /**
     * ✅ NOVO: Obter material específico do inventário
     */
    public InventoryItemDTO getMaterialFromInventory(Integer inventoryMaterialId) {
        InventoryMaterial material = inventoryMaterialRepository.findById(inventoryMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado no inventário"));
        return convertMaterialToItemDTO(material);
    }

    // ==================== BUY ITEMS ====================

    /**
     * ✅ CORRIGIDO: Comprar arma
     */
    public BuyItemResponse buyWeapon(UUID characterId, @NotNull(message = "Request é obrigatório") Integer weaponId) {
        log.info("Comprando arma: {} para personagem: {}", weaponId, characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));

        Inventory inventory = character.getInventory();

        // Buscar arma
        Weapon weapon = getWeaponEntity(weaponId);
        WeaponDTO weaponDTO = weaponService.convertToDTO(weapon);

        // ✅ CORRIGIDO: usar quantity = 1 (sem request.getQuantity())
        int quantity = 1;

        // Validar espaço
        if (inventory.getFreeSlots() < quantity) {
            throw new InvalidOperationException(
                    "Espaço insuficiente no inventário: " + quantity + " slots necessários"
            );
        }

        // Calcular custo total
        Long totalCost = weaponDTO.getTpCost() * quantity;

        // Validar TP
        if (character.getTp() < totalCost) {
            throw new InsufficientTPException(
                    "TP insuficiente. Necessário: " + totalCost + ", Disponível: " + character.getTp()
            );
        }

        // ✅ CORRETO: Usar inventoryWeaponRepository
        InventoryWeapon existingWeapon = inventoryWeaponRepository
                .findByInventory_Character_IdAndWeapon_Id(characterId, weaponId)
                .stream()
                .findFirst()
                .orElse(null);

        InventoryWeapon inventoryWeapon;
        if (existingWeapon != null) {
            existingWeapon.setQuantity(existingWeapon.getQuantity() + quantity);
            inventoryWeapon = inventoryWeaponRepository.save(existingWeapon);
        } else {
            inventoryWeapon = InventoryWeapon.builder()
                    .inventory(inventory)
                    .weapon(weapon)
                    .quantity(quantity)
                    .purchasedAtLevel(character.getLevel())
                    .acquiredAt(LocalDateTime.now())
                    .build();
            inventoryWeapon = inventoryWeaponRepository.save(inventoryWeapon);
        }

        // Descontar TP
        character.setTp((int) (character.getTp() - totalCost));
        characterRepository.save(character);

        // Atualizar slots
        inventory.setCurrentSlots(inventory.getCurrentSlots() + quantity);
        inventoryRepository.save(inventory);

        log.info("Compra realizada com sucesso");

        return BuyItemResponse.builder()
                .success(true)
                .message("Arma comprada com sucesso")
                .inventoryItemId(inventoryWeapon.getId())
                .itemId(weaponId)
                .itemName(weaponDTO.getName())
                .itemType("WEAPON")
                .quantity(quantity)
                .tpSpent(totalCost)
                .tpBalance(Long.valueOf(character.getTp()))
                .currentSlots(inventory.getCurrentSlots())
                .maxSlots(inventory.getMaxSlots())
                .slotsUsed(quantity)
                .purchaseSummary(String.format(
                        "Comprou %dx %s por %d TP",
                        quantity, weaponDTO.getName(), totalCost
                ))
                .build();
    }

    /**
     * ✅ CORRIGIDO: Comprar armadura
     */
    public BuyItemResponse buyArmor(UUID characterId, @NotNull(message = "Request é obrigatório") Integer armorId) {
        log.info("Comprando armadura: {} para personagem: {}", armorId, characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));

        Inventory inventory = character.getInventory();

        // Buscar armadura
        Armor armor = getArmorEntity(armorId);
        ArmorDTO armorDTO = armorService.convertToDTO(armor);

        // ✅ CORRIGIDO: usar quantity = 1 (sem request.getQuantity())
        int quantity = 1;

        // Validar espaço
        if (inventory.getFreeSlots() < quantity) {
            throw new InvalidOperationException(
                    "Espaço insuficiente no inventário: " + quantity + " slots necessários"
            );
        }

        // Calcular custo total
        Long totalCost = armorDTO.getTpCost() * quantity;

        // Validar TP
        if (character.getTp() < totalCost) {
            throw new InsufficientTPException(
                    "TP insuficiente. Necessário: " + totalCost + ", Disponível: " + character.getTp()
            );
        }

        // ✅ CORRETO: Usar inventoryArmorRepository
        InventoryArmor existingArmor = inventoryArmorRepository
                .findByInventory_Character_IdAndArmor_Id(characterId, armorId)
                .stream()
                .findFirst()
                .orElse(null);

        InventoryArmor inventoryArmor;
        if (existingArmor != null) {
            existingArmor.setQuantity(existingArmor.getQuantity() + quantity);
            inventoryArmor = inventoryArmorRepository.save(existingArmor);
        } else {
            inventoryArmor = InventoryArmor.builder()
                    .inventory(inventory)
                    .armor(armor)
                    .quantity(quantity)
                    .purchasedAtLevel(character.getLevel())
                    .acquiredAt(LocalDateTime.now())
                    .build();
            inventoryArmor = inventoryArmorRepository.save(inventoryArmor);
        }

        // Descontar TP
        character.setTp((int) (character.getTp() - totalCost));
        characterRepository.save(character);

        // Atualizar slots
        inventory.setCurrentSlots(inventory.getCurrentSlots() + quantity);
        inventoryRepository.save(inventory);

        log.info("Compra realizada com sucesso");

        return BuyItemResponse.builder()
                .success(true)
                .message("Armadura comprada com sucesso")
                .inventoryItemId(inventoryArmor.getId())
                .itemId(armorId)
                .itemName(armorDTO.getName())
                .itemType("ARMOR")
                .quantity(quantity)
                .tpSpent(totalCost)
                .tpBalance(Long.valueOf(character.getTp()))
                .currentSlots(inventory.getCurrentSlots())
                .maxSlots(inventory.getMaxSlots())
                .slotsUsed(quantity)
                .purchaseSummary(String.format(
                        "Comprou %dx %s por %d TP",
                        quantity, armorDTO.getName(), totalCost
                ))
                .build();
    }


    // ==================== SELL ITEMS ====================

    /**
     * ✅ NOVO: Vender arma específica
     */
    public SellItemResponse sellWeapon(UUID characterId, Integer inventoryWeaponId, Integer quantity) {
        log.info("Vendendo arma {} do personagem {}", inventoryWeaponId, characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));
        Inventory inventory = character.getInventory();

        InventoryWeapon weapon = inventoryWeaponRepository.findById((long)inventoryWeaponId)
                .orElseThrow(() -> new ResourceNotFoundException("Arma não encontrada"));

        if (weapon.getQuantity() < quantity) {
            throw new InvalidOperationException("Quantidade insuficiente para venda");
        }

        String itemName = weapon.getWeapon().getName();
        Long unitSellPrice = ((weapon.getWeapon().getTpCost() != null ? weapon.getWeapon().getTpCost() : 0) * 50) / 100;
        Long tpReceived = unitSellPrice * quantity;

        if (weapon.getQuantity().equals(quantity)) {
            inventoryWeaponRepository.delete(weapon);
        } else {
            weapon.setQuantity(weapon.getQuantity() - quantity);
            inventoryWeaponRepository.save(weapon);
        }

        character.setTp((int) (character.getTp() + tpReceived));
        characterRepository.save(character);
        inventory.setCurrentSlots(Math.max(0, inventory.getCurrentSlots() - quantity));
        inventoryRepository.save(inventory);

        return SellItemResponse.builder()
                .success(true)
                .message("Arma vendida com sucesso")
                .itemName(itemName)
                .itemType("WEAPON")
                .quantitySold(quantity)
                .tpReceived(tpReceived)
                .tpBalance((long) character.getTp())
                .currentSlots(inventory.getCurrentSlots())
                .maxSlots(inventory.getMaxSlots())
                .slotsFreed(quantity)
                .sellSummary(String.format("Vendeu %dx %s por %d TP", quantity, itemName, tpReceived))
                .build();
    }

    /**
     * ✅ NOVO: Vender armadura específica
     */
    public SellItemResponse sellArmor(UUID characterId, Integer inventoryArmorId, Integer quantity) {
        log.info("Vendendo armadura {} do personagem {}", inventoryArmorId, characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Personagem não encontrado"));
        Inventory inventory = character.getInventory();

        InventoryArmor armor = inventoryArmorRepository.findById((long)inventoryArmorId)
                .orElseThrow(() -> new ResourceNotFoundException("Armadura não encontrada"));

        if (armor.getQuantity() < quantity) {
            throw new InvalidOperationException("Quantidade insuficiente para venda");
        }

        String itemName = armor.getArmor().getName();
        Long unitSellPrice = ((armor.getArmor().getTpCost() != null ? armor.getArmor().getTpCost() : 0) * 50) / 100;
        Long tpReceived = unitSellPrice * quantity;

        if (armor.getQuantity().equals(quantity)) {
            inventoryArmorRepository.delete(armor);
        } else {
            armor.setQuantity(armor.getQuantity() - quantity);
            inventoryArmorRepository.save(armor);
        }

        character.setTp((int) (character.getTp() + tpReceived));
        characterRepository.save(character);
        inventory.setCurrentSlots(Math.max(0, inventory.getCurrentSlots() - quantity));
        inventoryRepository.save(inventory);

        return SellItemResponse.builder()
                .success(true)
                .message("Armadura vendida com sucesso")
                .itemName(itemName)
                .itemType("ARMOR")
                .quantitySold(quantity)
                .tpReceived(tpReceived)
                .tpBalance((long) character.getTp())
                .currentSlots(inventory.getCurrentSlots())
                .maxSlots(inventory.getMaxSlots())
                .slotsFreed(quantity)
                .sellSummary(String.format("Vendeu %dx %s por %d TP", quantity, itemName, tpReceived))
                .build();
    }


    // ==================== HELPER METHODS ====================

    /**
     * ✅ CORRETO: Buscar Weapon entity usando WeaponRepository
     */
    private Weapon getWeaponEntity(Integer weaponId) {
        if (weaponId == null) {
            throw new InvalidOperationException("ID da arma não pode ser nulo");
        }

        return weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Arma não encontrada com ID: " + weaponId
                ));
    }

    /**
     * ✅ CORRETO: Buscar Armor entity usando ArmorRepository
     */
    private Armor getArmorEntity(Integer armorId) {
        if (armorId == null) {
            throw new InvalidOperationException("ID da armadura não pode ser nulo");
        }

        return armorRepository.findById(armorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Armadura não encontrada com ID: " + armorId
                ));
    }

    /**
     * ✅ CORRETO - Materiais não tem preço
     */
    public long calculateInventoryValue(UUID characterId) {
        long weaponsValue = inventoryWeaponRepository.getTotalWeaponsValue(characterId);
        long armorsValue = inventoryArmorRepository.getTotalArmorsValue(characterId);

        // Materiais não têm valor TP (são apenas para crafting)
        return weaponsValue + armorsValue;
    }

    /**
     * ✅ CORRETO: Converter InventoryWeapon para DTO
     */
    private InventoryItemDTO convertWeaponToItemDTO(InventoryWeapon weapon) {
        WeaponDTO weaponDTO = weaponService.convertToDTO(weapon.getWeapon());
        return InventoryItemDTO.builder()
                .id(weapon.getId())
                .itemType("WEAPON")
                .weapon(weaponDTO)
                .quantity(weapon.getQuantity())
                .purchasedAtLevel(weapon.getPurchasedAtLevel())
                .acquiredAt(weapon.getAcquiredAt())
                .unitValue(weapon.getWeapon().getTpCost())
                .totalValue(weapon.getWeapon().getTpCost() * weapon.getQuantity())
                .sellValue(((weapon.getWeapon().getTpCost() * 50) / 100) * weapon.getQuantity())
                .isEquipped(false)
                .displayInfo(String.format("%dx %s", weapon.getQuantity(), weaponDTO.getDisplayName()))
                .build();
    }

    /**
     * ✅ CORRETO: Converter InventoryArmor para DTO
     */
    private InventoryItemDTO convertArmorToItemDTO(InventoryArmor armor) {
        ArmorDTO armorDTO = armorService.convertToDTO(armor.getArmor());
        return InventoryItemDTO.builder()
                .id(armor.getId())
                .itemType("ARMOR")
                .armor(armorDTO)
                .quantity(armor.getQuantity())
                .purchasedAtLevel(armor.getPurchasedAtLevel())
                .acquiredAt(armor.getAcquiredAt())
                .unitValue(armor.getArmor().getTpCost())
                .totalValue(armor.getArmor().getTpCost() * armor.getQuantity())
                .sellValue(((armor.getArmor().getTpCost() * 50) / 100) * armor.getQuantity())
                .isEquipped(false)
                .displayInfo(String.format("%dx %s", armor.getQuantity(), armorDTO.getDisplayName()))
                .build();
    }

    /**
     * ✅ CORRETO: Converter InventoryMaterial para DTO
     * Material não tem preço, apenas nome e quantidade
     */
    private InventoryItemDTO convertMaterialToItemDTO(InventoryMaterial material) {
        return InventoryItemDTO.builder()
                .id(material.getId())
                .itemType("MATERIAL")
                .quantity(material.getQuantity())
                .purchasedAtLevel(material.getPurchasedAtLevel())
                .acquiredAt(material.getAcquiredAt())
                .displayInfo(String.format("%dx %s", material.getQuantity(), material.getMaterial().getName()))
                .build();
    }

}
