package com.mugen.backend.repository.transformation;

import com.mugen.backend.entity.character.CharacterTransformation;
import com.mugen.backend.entity.character.CharacterTransformationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterTransformationRepository extends JpaRepository<CharacterTransformation, CharacterTransformationId> {

    // Buscar transformações de um personagem
    @Query("SELECT ct FROM CharacterTransformation ct WHERE ct.character.id = :characterId")
    List<CharacterTransformation> findByCharacterId(@Param("characterId") UUID characterId);

    // Buscar transformações desbloqueadas
    @Query("SELECT ct FROM CharacterTransformation ct WHERE ct.character.id = :characterId AND ct.unlocked = true")
    List<CharacterTransformation> findUnlockedByCharacterId(@Param("characterId") UUID characterId);

    // Verificar se personagem tem transformação específica desbloqueada
    @Query("SELECT ct FROM CharacterTransformation ct WHERE " +
            "ct.id.characterId = :characterId AND " +
            "ct.id.transformationId = :transformationId AND " +
            "ct.unlocked = true")
    Optional<CharacterTransformation> findUnlockedTransformation(
            @Param("characterId") UUID characterId,
            @Param("transformationId") Integer transformationId
    );
}
