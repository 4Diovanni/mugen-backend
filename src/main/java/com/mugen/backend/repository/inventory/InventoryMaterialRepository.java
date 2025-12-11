package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.InventoryMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para InventoryMaterial
 * Gerencia materiais no inventário do personagem
 * ✅ 16 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface InventoryMaterialRepository extends JpaRepository<InventoryMaterial, Integer> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca material no inventário
     * ✅ ESSENCIAL para InventoryService
     */
    Optional<InventoryMaterial> findByInventory_CharacterIdAndMaterial_Id(UUID characterId, Integer materialId);

    /**
     * Lista todos os materiais do personagem
     * ✅ ESSENCIAL para getCharacterMaterials()
     */
    List<InventoryMaterial> findByInventory_CharacterIdOrderByAcquiredAtDesc(UUID characterId);

    /**
     * ✅ NOVO: Buscar materiais por ID do inventário
     * Muito mais eficiente que findAll().stream().filter()
     */
    List<InventoryMaterial> findByInventoryId(Long inventoryId);

    /**
     * ✅ OPCIONAL: Buscar materiais de um personagem por ID do material
     * Alternativa ao findByInventory_CharacterIdAndMaterial_Id
     */
    List<InventoryMaterial> findByInventory_Character_IdAndMaterial_Id(UUID characterId, Integer materialId);

    // ==================== BUSCAS CUSTOMIZADAS ====================

    /**
     * Lista materiais do personagem por raridade
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.material.rarity = :rarity ORDER BY im.material.name ASC")
    List<InventoryMaterial> findByCharacterAndRarity(
            @Param("characterId") UUID characterId,
            @Param("rarity") com.mugen.backend.enums.WeaponRarity rarity);

    /**
     * Busca materiais com quantidade baixa (< 5)
     * Para alertas de falta de recursos
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.quantity < 5 ORDER BY im.quantity ASC")
    List<InventoryMaterial> findLowQuantityMaterials(@Param("characterId") UUID characterId);

    /**
     * Busca materiais com quantidade alta (> 10)
     * Para detecção de excesso
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.quantity > 10 ORDER BY im.quantity DESC")
    List<InventoryMaterial> findHighQuantityMaterials(@Param("characterId") UUID characterId);

    /**
     * Busca materiais lendários
     * Para itens especiais
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.material.rarity = 'LENDARIO'")
    List<InventoryMaterial> findLegendaryMaterials(@Param("characterId") UUID characterId);

    /**
     * Busca materiais épicos
     * Para itens raros
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.material.rarity = 'EPICO'")
    List<InventoryMaterial> findEpicMaterials(@Param("characterId") UUID characterId);

    /**
     * Conta quantidade total de um material específico
     * ✅ ESSENCIAL para verificar se pode vender/usar
     */
    @Query("SELECT COALESCE(SUM(im.quantity), 0) FROM InventoryMaterial im " +
            "WHERE im.inventory.character.id = :characterId AND im.material.id = :materialId")
    long getTotalQuantityOfMaterial(
            @Param("characterId") UUID characterId,
            @Param("materialId") Integer materialId);

    /**
     * Calcula quantidade total de materiais do personagem
     * ✅ ESSENCIAL para getTotalMaterials()
     */
    @Query("SELECT COALESCE(SUM(im.quantity), 0) FROM InventoryMaterial im " +
            "WHERE im.inventory.character.id = :characterId")
    long getTotalMaterialsQuantity(@Param("characterId") UUID characterId);

    /**
     * Conta quantos tipos diferentes de materiais o personagem tem
     * Para análise de diversidade
     */
    @Query("SELECT COUNT(DISTINCT im.material.id) FROM InventoryMaterial im " +
            "WHERE im.inventory.character.id = :characterId")
    long countDifferentMaterialTypes(@Param("characterId") UUID characterId);

    /**
     * Verifica se personagem tem um material específico
     */
    boolean existsByInventory_CharacterIdAndMaterial_Id(UUID characterId, Integer materialId);

    /**
     * Delete materiais por personagem (limpeza de dados)
     */
    void deleteByInventory_Character_Id(UUID characterId);

    /**
     * Busca materiais por quantidade mínima
     * Útil para crafting
     */
    @Query("SELECT im FROM InventoryMaterial im WHERE im.inventory.character.id = :characterId " +
            "AND im.quantity >= :minQuantity ORDER BY im.quantity DESC")
    List<InventoryMaterial> findByQuantityGreaterThanOrEqual(
            @Param("characterId") UUID characterId,
            @Param("minQuantity") Integer minQuantity);


}
