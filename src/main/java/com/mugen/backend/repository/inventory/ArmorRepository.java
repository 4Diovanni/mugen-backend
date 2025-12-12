package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.Armor;
import com.mugen.backend.enums.ArmorRarity;
import com.mugen.backend.enums.ArmorType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Armor (Armadura)
 * Queries para buscar armaduras com diversos filtros
 * 14 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface ArmorRepository extends JpaRepository<Armor, Integer> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca armadura por nome
     */
    Optional<Armor> findByName(String name);

    /**
     * Lista todas as armaduras disponíveis
     */
    List<Armor> findAllByIsActiveTrue();

    /**
     * Lista armaduras ordenadas por raridade
     */
    List<Armor> findByIsActiveTrueOrderByRarityDesc();

    // ==================== FILTROS ====================

    /**
     * Busca armaduras por tipo (LEVE, NORMAL, PESADA)
     */
    List<Armor> findByArmorTypeAndIsActiveTrueOrderByNameAsc(ArmorType armorType);

    /**
     * Busca armaduras por raridade
     */
    List<Armor> findByRarityAndIsActiveTrueOrderByNameAsc(
            @NotNull(message = "Raridade não pode ser nula") ArmorRarity rarity);

    /**
     * Busca armaduras por tipo E raridade
     */
    List<Armor> findByArmorTypeAndRarityAndIsActiveTrueOrderByNameAsc(
            @NotNull(message = "Tipo não pode ser nulo") ArmorType armorType,
            @NotNull(message = "Raridade não pode ser nula") ArmorRarity rarity);

    // ==================== BUSCAS COMPLEXAS ====================

    /**
     * Busca armaduras leves (para mobilidade)
     */
    @Query("SELECT a FROM Armor a WHERE a.armorType = 'LEVE' AND a.isActive = true " +
            "ORDER BY a.rarity DESC")
    List<Armor> findLightArmor();

    /**
     * Busca armaduras pesadas (para defesa)
     */
    @Query("SELECT a FROM Armor a WHERE a.armorType = 'PESADA' AND a.isActive = true " +
            "ORDER BY a.rarity DESC")
    List<Armor> findHeavyArmor();

    /**
     * Busca armaduras normais (balanceadas)
     */
    @Query("SELECT a FROM Armor a WHERE a.armorType = 'NORMAL' AND a.isActive = true " +
            "ORDER BY a.rarity DESC")
    List<Armor> findNormalArmor();

    /**
     * 🔧 QUERY CRÍTICO - Busca armaduras que o personagem pode usar
     * Trata NULL values nos requisitos
     */
    @Query("SELECT a FROM Armor a WHERE a.minLevel <= :level AND a.minCon <= :con " +
            "AND a.isActive = true ORDER BY a.rarity DESC")
    List<Armor> findArmorThatCharacterCanUse(
            @Param("level") Integer level,
            @Param("con") Integer con);

    /**
     * Busca armaduras que o personagem NÃO pode usar
     */
    @Query("SELECT a FROM Armor a WHERE (a.minLevel > :level OR a.minCon > :con) " +
            "AND a.isActive = true ORDER BY a.rarity DESC")
    List<Armor> findArmorThatCharacterCannotUse(
            @Param("level") Integer level,
            @Param("con") Integer con);

    /**
     * Busca armaduras por faixa de preço
     */
    @Query("SELECT a FROM Armor a WHERE a.tpCost BETWEEN :minPrice AND :maxPrice " +
            "AND a.isActive = true ORDER BY a.tpCost ASC")
    List<Armor> findByPriceRange(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice);

    /**
     * Busca armaduras épicas e lendárias
     */
    @Query("SELECT a FROM Armor a WHERE a.rarity IN ('LENDARIO', 'EPICO') " +
            "AND a.isActive = true ORDER BY a.rarity DESC")
    List<Armor> findRareArmor();

    /**
     * Conta armaduras por tipo
     */
    @Query("SELECT COUNT(a) FROM Armor a WHERE a.armorType = :armorType AND a.isActive = true")
    long countByArmorType(@Param("armorType") ArmorType armorType);
}
