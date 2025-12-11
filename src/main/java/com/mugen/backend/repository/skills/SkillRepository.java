package com.mugen.backend.repository.skills;

import com.mugen.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    // ========================================
    // CONSULTAS BÁSICAS (Spring Data Naming)
    // ========================================

    /**
     * Buscar skills ativas
     */
    List<Skill> findByIsActiveTrue();

    /**
     * Buscar skills por nome (parcial, case-insensitive)
     */
    List<Skill> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar skill por nome exato
     */
    Optional<Skill> findByName(String name);

    /**
     * Verificar se existe skill com o mesmo nome
     */
    boolean existsByName(String name);

    /**
     * Buscar skills ordenadas por custo de TP
     */
    List<Skill> findAllByOrderByBaseTpCostAsc();

    // ========================================
    // CONSULTAS CUSTOMIZADAS (@Query)
    // ========================================

    /**
     * Buscar skills por tipo
     * Nota: usando skillType do banco de dados
     */
    @Query("SELECT s FROM Skill s WHERE s.skillType = :type AND s.isActive = true")
    List<Skill> findByType(@Param("type") String type);

    /**
     * Buscar todas skills ativas (query alternativa)
     */
    @Query("SELECT s FROM Skill s WHERE s.isActive = true ORDER BY s.name ASC")
    List<Skill> findAllActive();

    /**
     * Buscar skills por nível requerido máximo
     */
    @Query("SELECT s FROM Skill s WHERE s.requiredLevel <= :level AND s.isActive = true ORDER BY s.requiredLevel ASC")
    List<Skill> findByRequiredLevelLessThanEqual(@Param("level") Integer level);

    /**
     * Buscar skills disponíveis para um personagem (baseado no nível)
     */
    @Query("SELECT s FROM Skill s WHERE s.requiredLevel <= :characterLevel AND s.isActive = true ORDER BY s.requiredLevel ASC, s.name ASC")
    List<Skill> findAvailableSkillsForLevel(@Param("characterLevel") Integer characterLevel);

    /**
     * Buscar skills por custo máximo de TP
     */
    @Query("SELECT s FROM Skill s WHERE s.baseTpCost <= :maxCost AND s.isActive = true ORDER BY s.baseTpCost ASC")
    List<Skill> findByMaxTpCost(@Param("maxCost") Integer maxCost);

    /**
     * Buscar skills por tipo e nível
     */
    @Query("SELECT s FROM Skill s WHERE s.skillType = :type AND s.requiredLevel <= :level AND s.isActive = true")
    List<Skill> findByTypeAndLevel(@Param("type") String type, @Param("level") Integer level);
}
