package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.InventoryArmor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para InventoryArmor
 * Gerencia armaduras no inventário do personagem
 * ✅ 15 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface InventoryArmorRepository extends JpaRepository<InventoryArmor, Long> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca armadura no inventário
     * ✅ ESSENCIAL para InventoryService
     */
//    Optional<InventoryArmor> findByInventory_CharacterIdAndArmor_Id(UUID characterId, Integer armorId);

    // InventoryArmorRepository.java
    Optional<InventoryArmor> findByInventory_Character_IdAndArmor_Id(UUID characterId, Integer armorId);

    /**
     * Lista todas as armaduras do personagem
     * ✅ ESSENCIAL para getCharacterArmors()
     */
    List<InventoryArmor> findByInventory_CharacterIdOrderByAcquiredAtDesc(UUID characterId);

    /**
     * ✅ NOVO: Buscar armaduras por ID do inventário
     * Muito mais eficiente que findAll().stream().filter()
     */
    List<InventoryArmor> findByInventoryId(Long inventoryId);

//    /**
//     * ✅ OPCIONAL: Buscar armaduras de um personagem por ID da armadura
//     * Alternativa ao findByInventory_CharacterIdAndArmor_Id
//     */
//    List<InventoryArmor> findByInventory_Character_IdAndArmor_Id(UUID characterId, Integer armorId);

    // ==================== BUSCAS CUSTOMIZADAS ====================

    /**
     * Lista armaduras do personagem por tipo
     * Filtro por LEVE, NORMAL, PESADA
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.armor.armorType = :armorType ORDER BY ia.armor.rarity DESC")
    List<InventoryArmor> findByCharacterAndArmorType(
            @Param("characterId") UUID characterId,
            @Param("armorType") com.mugen.backend.enums.ArmorType armorType);

    /**
     * Lista armaduras do personagem por raridade
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.armor.rarity = :rarity ORDER BY ia.armor.name ASC")
    List<InventoryArmor> findByCharacterAndRarity(
            @Param("characterId") UUID characterId,
            @Param("rarity") com.mugen.backend.enums.WeaponRarity rarity);

    /**
     * Busca armaduras duplicadas
     * Para gerenciar extras
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.quantity > 1 ORDER BY ia.quantity DESC")
    List<InventoryArmor> findDuplicateArmors(@Param("characterId") UUID characterId);

    /**
     * Busca armaduras leves
     * Para mobilidade
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.armor.armorType = 'LEVE' ORDER BY ia.armor.rarity DESC")
    List<InventoryArmor> findLightArmors(@Param("characterId") UUID characterId);

    /**
     * Busca armaduras pesadas
     * Para defesa
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.armor.armorType = 'PESADA' ORDER BY ia.armor.rarity DESC")
    List<InventoryArmor> findHeavyArmors(@Param("characterId") UUID characterId);

    /**
     * Busca armaduras normais
     * Para balanceamento
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "AND ia.armor.armorType = 'NORMAL' ORDER BY ia.armor.rarity DESC")
    List<InventoryArmor> findNormalArmors(@Param("characterId") UUID characterId);

    /**
     * Calcula valor total de armaduras do personagem
     * ✅ ESSENCIAL para calculateInventoryValue()
     */
    @Query("SELECT COALESCE(SUM(ia.armor.tpCost * ia.quantity), 0) FROM InventoryArmor ia " +
            "WHERE ia.inventory.character.id = :characterId")
    long getTotalArmorsValue(@Param("characterId") UUID characterId);

    /**
     * Conta quantidade total de armaduras do personagem
     * ✅ ESSENCIAL para getTotalArmors()
     */
    @Query("SELECT COALESCE(SUM(ia.quantity), 0) FROM InventoryArmor ia " +
            "WHERE ia.inventory.character.id = :characterId")
    long getTotalArmorsQuantity(@Param("characterId") UUID characterId);

    /**
     * Busca armadura mais valiosa
     * Para análise de fortuna
     */
    @Query("SELECT ia FROM InventoryArmor ia WHERE ia.inventory.character.id = :characterId " +
            "ORDER BY ia.armor.tpCost DESC LIMIT 1")
    Optional<InventoryArmor> findMostValuableArmor(@Param("characterId") UUID characterId);

    /**
     * Verifica se personagem tem uma armadura específica
     */
    boolean existsByInventory_CharacterIdAndArmor_Id(UUID characterId, Integer armorId);

    /**
     * Delete armaduras por personagem (limpeza de dados)
     */
    void deleteByInventory_Character_Id(UUID characterId);
}
