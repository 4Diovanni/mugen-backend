package com.mugen.backend.repository;

import com.mugen.backend.entity.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, Integer> {

    // Buscar por nome
    List<Transformation> findByNameContainingIgnoreCase(String name);

    // Buscar apenas transformações ativas
    @Query("SELECT t FROM Transformation t WHERE t.isActive = true")
    List<Transformation> findAllActive();

    // Buscar transformações por raça
    @Query("SELECT t FROM Transformation t WHERE t.race.id = :raceId AND t.raceRequired = true")
    List<Transformation> findByRaceId(@Param("raceId") Integer raceId);

    // Buscar transformações universais (não requerem raça específica)
    @Query("SELECT t FROM Transformation t WHERE t.raceRequired = false")
    List<Transformation> findUniversalTransformations();

    // Buscar transformações disponíveis para um personagem (por nível e raça)
    @Query("SELECT t FROM Transformation t WHERE " +
            "t.isActive = true AND " +
            "t.requiredLevel <= :level AND " +
            "(t.raceRequired = false OR t.race.id = :raceId)")
    List<Transformation> findAvailableForCharacter(
            @Param("level") Integer level,
            @Param("raceId") Integer raceId
    );
}
