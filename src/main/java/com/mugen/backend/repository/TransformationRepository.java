package com.mugen.backend.repository;

import com.mugen.backend.entity.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, Integer> {

    List<Transformation> findByIsActiveTrue();

    List<Transformation> findByRaceId(Integer raceId);

    List<Transformation> findByRaceRequiredFalse();

    @Query("SELECT t FROM Transformation t WHERE t.raceRequired = false OR t.race.id = :raceId")
    List<Transformation> findAvailableForRace(Integer raceId);
}
