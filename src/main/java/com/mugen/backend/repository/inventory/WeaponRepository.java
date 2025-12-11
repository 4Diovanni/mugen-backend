package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.Weapon;
import com.mugen.backend.enums.WeaponRarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Weapon (Arma)
 * Responsável por todas as queries de Armas
 * Tratamento de NULL values e relacionamentos com LEFT JOIN
 * ✅ 13 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Integer> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Buscar armas ativas
     * Este é um método simples que Spring Data JPA gera automaticamente
     */
    Page<Weapon> findByIsActive(boolean isActive, Pageable pageable);

    /**
     * Buscar armas por ID (se precisar de query customizada)
     * Nota: findById() já existe automaticamente
     */
    @Query("SELECT w FROM Weapon w WHERE w.id = :id AND w.isActive = true")
    Optional<Weapon> findActiveById(@Param("id") Integer id);

    /**
     * Buscar todas armas ativas
     */
    @Query("SELECT w FROM Weapon w WHERE w.isActive = true ORDER BY w.name ASC")
    List<Weapon> findAllActive();

    // ==================== BUSCAS POR TIPO ====================

    /**
     * Buscar armas por tipo primário
     */
    @Query("SELECT w FROM Weapon w WHERE w.primaryType = :type AND w.isActive = true ORDER BY w.name ASC")
    List<Weapon> findByPrimaryType(@Param("type") String primaryType);

    /**
     * Buscar armas por tipo secundário
     */
    @Query("SELECT w FROM Weapon w WHERE w.secondaryType = :type AND w.isActive = true ORDER BY w.name ASC")
    List<Weapon> findBySecondaryType(@Param("type") String secondaryType);

    /**
     * Buscar armas por tipo de elemento
     */
    @Query("SELECT w FROM Weapon w WHERE w.elementalType = :element AND w.isActive = true ORDER BY w.name ASC")
    List<Weapon> findByElementalType(@Param("element") String element);

    /**
     * Buscar armas por raridade
     */
    @Query("SELECT w FROM Weapon w WHERE w.rarity = :rarity AND w.isActive = true ORDER BY w.tpCost DESC")
    List<Weapon> findByRarity(@Param("rarity") WeaponRarity rarity);

    /**
     * Contar armas por raridade
     */
    @Query("SELECT COUNT(w) FROM Weapon w WHERE w.rarity = :rarity AND w.isActive = true")
    long countByRarity(@Param("rarity") WeaponRarity rarity);

    // ==================== BUSCAS ESPECIAIS ====================

    /**
     * Buscar todas armas ativas e únicas
     */
    @Query("SELECT w FROM Weapon w WHERE w.isActive = true AND w.isUnique = true ORDER BY w.rarity DESC")
    List<Weapon> findActiveUniqueWeapons();

    /**
     * 🔧 QUERY CRÍTICO - Buscar armas que o personagem pode equipar
     * Usa LEFT JOIN para acessar WeaponRequirements
     * Trata NULL values (armas sem requisitos)
     * DISTINCT para evitar duplicatas
     */
    @Query("SELECT DISTINCT w FROM Weapon w " +
            "LEFT JOIN w.requirements r " +
            "WHERE (r IS NULL OR r.minStr IS NULL OR r.minStr <= :str) " +
            "AND (r IS NULL OR r.minDex IS NULL OR r.minDex <= :dex) " +
            "AND (r IS NULL OR r.minCon IS NULL OR r.minCon <= :con) " +
            "AND (r IS NULL OR r.minWil IS NULL OR r.minWil <= :wil) " +
            "AND (r IS NULL OR r.minMnd IS NULL OR r.minMnd <= :mnd) " +
            "AND (r IS NULL OR r.minSpi IS NULL OR r.minSpi <= :spi) " +
            "AND (r IS NULL OR r.minLevel IS NULL OR r.minLevel <= :level) " +
            "AND w.isActive = true " +
            "ORDER BY w.rarity DESC")
    Page<Weapon> findByPlayerStats(
            @Param("str") Integer str,
            @Param("dex") Integer dex,
            @Param("con") Integer con,
            @Param("wil") Integer wil,
            @Param("mnd") Integer mnd,
            @Param("spi") Integer spi,
            @Param("level") Integer level,
            Pageable pageable
    );

    /**
     * Buscar armas por tipo primário E elemento (combinado)
     * Útil para filtros avançados
     */
    @Query("SELECT w FROM Weapon w " +
            "WHERE w.primaryType = :primaryType " +
            "AND w.elementalType = :element " +
            "AND w.isActive = true " +
            "ORDER BY w.rarity DESC")
    List<Weapon> findByPrimaryTypeAndElement(
            @Param("primaryType") String primaryType,
            @Param("element") String element
    );

    /**
     * Buscar armas mais baratas de um tipo
     */
    @Query("SELECT w FROM Weapon w " +
            "WHERE w.primaryType = :type " +
            "AND w.isActive = true " +
            "ORDER BY w.tpCost ASC")
    Page<Weapon> findCheapestByType(
            @Param("type") String type,
            Pageable pageable
    );

    /**
     * Buscar armas entre um range de preço
     */
    @Query("SELECT w FROM Weapon w " +
            "WHERE w.tpCost >= :minPrice " +
            "AND w.tpCost <= :maxPrice " +
            "AND w.isActive = true " +
            "ORDER BY w.tpCost ASC")
    List<Weapon> findByPriceRange(
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice
    );
}
