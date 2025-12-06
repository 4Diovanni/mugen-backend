package com.mugen.backend.repository;

import com.mugen.backend.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

    Optional<Achievement> findByKeyName(String key_Name);

    List<Achievement> findByCategory(String category);

    List<Achievement> findByIsActiveTrue();

    List<Achievement> findByCategoryAndIsActiveTrue(String category);

    Long countByIsActiveTrue();

    @Query("SELECT a FROM Achievement a WHERE a.category = :category ORDER BY a.createdAt DESC")
    List<Achievement> findLatestByCategory(String category);
}
