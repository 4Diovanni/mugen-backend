package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.InventoryWeapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para InventoryWeapon
 * Gerencia armas no inventário do personagem
 * ✅ 13 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface InventoryWeaponRepository extends JpaRepository<InventoryWeapon, Long> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca arma no inventário
     * ✅ ESSENCIAL para InventoryService
     */
    Optional<InventoryWeapon> findByInventory_CharacterIdAndWeapon_Id(UUID characterId, Integer weaponId);

    /**
     * Lista todas as armas do personagem
     * ✅ ESSENCIAL para getCharacterWeapons()
     */
    List<InventoryWeapon> findByInventory_CharacterIdOrderByAcquiredAtDesc(UUID characterId);

    /**
     * ✅ NOVO: Buscar armas por ID do inventário
     * Muito mais eficiente que findAll().stream().filter()
     */
    List<InventoryWeapon> findByInventoryId(Long inventoryId);

    /**
     * ✅ OPCIONAL: Buscar armas de um personagem por ID da arma
     * Alternativa ao findByInventory_CharacterIdAndWeapon_Id
     */
    List<InventoryWeapon> findByInventory_Character_IdAndWeapon_Id(UUID characterId, Integer weaponId);

    // ==================== BUSCAS CUSTOMIZADAS ====================

    /**
     * Lista armas do personagem por raridade
     * Útil para listar armas raras
     */
    @Query("SELECT iw FROM InventoryWeapon iw WHERE iw.inventory.character.id = :characterId " +
            "AND iw.weapon.rarity = :rarity ORDER BY iw.weapon.name ASC")
    List<InventoryWeapon> findByCharacterAndRarity(
            @Param("characterId") UUID characterId,
            @Param("rarity") com.mugen.backend.enums.WeaponRarity rarity);

    /**
     * Busca armas duplicadas (quantidade > 1)
     * Para gerenciar armas extras
     */
    @Query("SELECT iw FROM InventoryWeapon iw WHERE iw.inventory.character.id = :characterId " +
            "AND iw.quantity > 1 ORDER BY iw.quantity DESC")
    List<InventoryWeapon> findDuplicateWeapons(@Param("characterId") UUID characterId);

    /**
     * Busca armas únicas do personagem
     * Mostrar troféus/armas especiais
     */
    @Query("SELECT iw FROM InventoryWeapon iw WHERE iw.inventory.character.id = :characterId " +
            "AND iw.weapon.isUnique = true")
    List<InventoryWeapon> findUniqueWeapons(@Param("characterId") UUID characterId);

    /**
     * Calcula valor total de armas do personagem
     * ✅ ESSENCIAL para calculateInventoryValue()
     */
    @Query("SELECT COALESCE(SUM(iw.weapon.tpCost * iw.quantity), 0) FROM InventoryWeapon iw " +
            "WHERE iw.inventory.character.id = :characterId")
    long getTotalWeaponsValue(@Param("characterId") UUID characterId);

    /**
     * Conta quantidade total de armas do personagem
     * ✅ ESSENCIAL para getTotalWeapons()
     */
    @Query("SELECT COALESCE(SUM(iw.quantity), 0) FROM InventoryWeapon iw " +
            "WHERE iw.inventory.character.id = :characterId")
    long getTotalWeaponsQuantity(@Param("characterId") UUID characterId);

    /**
     * Busca arma mais valiosa do personagem
     * Para análise de fortuna
     */
    @Query("SELECT iw FROM InventoryWeapon iw WHERE iw.inventory.character.id = :characterId " +
            "ORDER BY iw.weapon.tpCost DESC LIMIT 1")
    Optional<InventoryWeapon> findMostValuableWeapon(@Param("characterId") UUID characterId);

    /**
     * Busca armas adquiridas num range de level
     * Para progressão do personagem
     */
    @Query("SELECT iw FROM InventoryWeapon iw WHERE iw.inventory.character.id = :characterId " +
            "AND iw.purchasedAtLevel BETWEEN :minLevel AND :maxLevel")
    List<InventoryWeapon> findWeaponsAcquiredBetweenLevels(
            @Param("characterId") UUID characterId,
            @Param("minLevel") Integer minLevel,
            @Param("maxLevel") Integer maxLevel);

    /**
     * Verifica se personagem tem uma arma específica
     */
    boolean existsByInventory_CharacterIdAndWeapon_Id(UUID characterId, Integer weaponId);

    /**
     * Delete armas por personagem (limpeza de dados)
     */
    void deleteByInventory_Character_Id(UUID characterId);
}
