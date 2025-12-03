package com.mugen.backend.repository;

import com.mugen.backend.entity.CharacterSkill;
import com.mugen.backend.entity.CharacterSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterSkillRepository extends JpaRepository<CharacterSkill, CharacterSkillId> {

    // Buscar todas as skills de um personagem
    @Query("SELECT cs FROM CharacterSkill cs WHERE cs.character.id = :characterId")
    List<CharacterSkill> findByCharacterId(@Param("characterId") UUID characterId);

    // Buscar skill específica de um personagem
    @Query("SELECT cs FROM CharacterSkill cs WHERE cs.id.characterId = :characterId AND cs.id.skillId = :skillId")
    Optional<CharacterSkill> findByCharacterIdAndSkillId(
            @Param("characterId") UUID characterId,
            @Param("skillId") Integer skillId
    );

    // Contar skills de um personagem
    @Query("SELECT COUNT(cs) FROM CharacterSkill cs WHERE cs.character.id = :characterId")
    Long countByCharacterId(@Param("characterId") UUID characterId);
}
