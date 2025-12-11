package com.mugen.backend.repository.inventory;

import com.mugen.backend.entity.inventory.Material;
import com.mugen.backend.enums.WeaponRarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Material
 * Queries para gerenciar materiais (adicionados pelo MASTER)
 * ✅ 10 MÉTODOS IMPLEMENTADOS
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    // ==================== BUSCA BÁSICA ====================

    /**
     * Busca material por nome
     */
    Optional<Material> findByName(String name);

    /**
     * Lista todos os materiais ordenados por nome (ASC)
     */
    List<Material> findAllByOrderByNameAsc();

    // ==================== FILTROS ====================

    /**
     * Busca materiais por raridade
     */
    List<Material> findByRarityOrderByNameAsc(WeaponRarity rarity);

    /**
     * Busca materiais lendários
     */
    @Query("SELECT m FROM Material m WHERE m.rarity = 'LENDARIO' ORDER BY m.name ASC")
    List<Material> findLegendaryMaterials();

    /**
     * Busca materiais épicos
     */
    @Query("SELECT m FROM Material m WHERE m.rarity = 'EPICO' ORDER BY m.name ASC")
    List<Material> findEpicMaterials();

    /**
     * Busca materiais raros
     */
    @Query("SELECT m FROM Material m WHERE m.rarity = 'RARO' ORDER BY m.name ASC")
    List<Material> findRareMaterials();

    /**
     * Busca materiais comuns
     */
    @Query("SELECT m FROM Material m WHERE m.rarity IN ('COMUM', 'POBRE') ORDER BY m.name ASC")
    List<Material> findCommonMaterials();

    /**
     * Verifica se foi adicionado pelo MASTER
     */
    @Query("SELECT m FROM Material m WHERE m.addedByMaster = true ORDER BY m.name ASC")
    List<Material> findAllAddedByMaster();

    /**
     * Conta materiais por raridade
     */
    @Query("SELECT COUNT(m) FROM Material m WHERE m.rarity = :rarity")
    long countByRarity(@Param("rarity") WeaponRarity rarity);

    /**
     * Busca materiais ativos
     */
    /**
     * Busca todos os materiais válidos (adicionados pelo MASTER)
     * ✅ CORRETO: Material não tem isActive, usa addedByMaster
     */
    @Query("SELECT m FROM Material m WHERE m.addedByMaster = true ORDER BY m.name ASC")
    List<Material> findAllValid();
}
