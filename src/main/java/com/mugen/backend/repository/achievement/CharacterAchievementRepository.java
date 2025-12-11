package com.mugen.backend.repository.achievement;

import com.mugen.backend.entity.character.CharacterAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterAchievementRepository extends JpaRepository<CharacterAchievement, Long> {

    List<CharacterAchievement> findByCharacterId(UUID characterId);

    List<CharacterAchievement> findByCharacterIdOrderByObtainedAtDesc(UUID characterId);

    Optional<CharacterAchievement> findByCharacterIdAndAchievementId(UUID characterId, Integer achievementId);

    Long countByCharacterId(UUID characterId);

    @Query("SELECT SUM(a.rewardTp) FROM CharacterAchievement ca " +
            "JOIN ca.achievement a WHERE ca.character.id = :characterId")
    Integer sumTpByCharacterId(UUID characterId);

    @Query("SELECT ca FROM CharacterAchievement ca " +
            "JOIN ca.achievement a " +
            "WHERE ca.character.id = :characterId " +
            "AND a.category = :category " +
            "ORDER BY ca.obtainedAt DESC")
    List<CharacterAchievement> findByCharacterIdAndCategory(UUID characterId, String category);

    @Query("SELECT COUNT(DISTINCT ca.achievement.id) FROM CharacterAchievement ca " +
            "WHERE ca.character.id = :characterId")
    Integer countUniqueAchievements(UUID characterId);
}
