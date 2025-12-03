package com.mugen.backend.repository;

import com.mugen.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    // Buscar por nome
    List<Skill> findByNameContainingIgnoreCase(String name);

    // Buscar por tipo
    @Query("SELECT s FROM Skill s WHERE s.skillType = :type")
    List<Skill> findByType(@Param("type") String type);

    // Buscar apenas skills ativas
    @Query("SELECT s FROM Skill s WHERE s.isActive = true")
    List<Skill> findAllActive();

    // Buscar skills ordenadas por custo
    List<Skill> findAllByOrderByBaseTpCostAsc();
}
