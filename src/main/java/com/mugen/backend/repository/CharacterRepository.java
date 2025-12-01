package com.mugen.backend.repository;

import com.mugen.backend.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {

    List<Character> findByOwnerId(UUID ownerId);

    List<Character> findByOwnerIdAndIsActiveTrue(UUID ownerId);

    @Query("SELECT c FROM Character c JOIN FETCH c.race WHERE c.id = :id")
    Optional<Character> findByIdWithRace(UUID id);

    @Query("SELECT c FROM Character c JOIN FETCH c.attributes WHERE c.id = :id")
    Optional<Character> findByIdWithAttributes(UUID id);

    long countByOwnerId(UUID ownerId);

    boolean existsByOwnerIdAndName(UUID ownerId, String name);
}
