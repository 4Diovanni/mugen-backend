package com.mugen.backend.repository;

import com.mugen.backend.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {

    // Buscar personagens por owner
    List<Character> findByOwnerId(UUID ownerId);

    // ✅ ADICIONE ESTE MÉTODO
    // Buscar personagens ativos por owner (Spring Data naming convention)
    List<Character> findByOwnerIdAndIsActiveTrue(UUID ownerId);

    // OU use query customizada (alternativa)
    @Query("SELECT c FROM Character c WHERE c.owner.id = :ownerId AND c.isActive = true")
    List<Character> findActiveByOwnerId(@Param("ownerId") UUID ownerId);

    // ✅ ADICIONE ESTE MÉTODO
    // Buscar com raça carregada
    @Query("SELECT c FROM Character c LEFT JOIN FETCH c.race WHERE c.id = :id")
    Optional<Character> findByIdWithRace(@Param("id") UUID id);

    // Buscar com atributos carregados
    @Query("SELECT c FROM Character c LEFT JOIN FETCH c.attributes WHERE c.id = :id")
    Optional<Character> findByIdWithAttributes(@Param("id") UUID id);

    // Buscar com TUDO carregado (use com cuidado - pode ser pesado)
    @Query("SELECT DISTINCT c FROM Character c " +
            "LEFT JOIN FETCH c.attributes " +
            "LEFT JOIN FETCH c.race " +
            "LEFT JOIN FETCH c.activeTransformation " +
            "WHERE c.id = :id")
    Optional<Character> findByIdWithFullDetails(@Param("id") UUID id);

    // Verificar se nome já existe para o mesmo owner
    boolean existsByOwnerIdAndName(UUID ownerId, String name);

    // Contar personagens por owner
    Long countByOwnerId(UUID ownerId);

    // Buscar personagens por raça
    @Query("SELECT c FROM Character c WHERE c.race.id = :raceId")
    List<Character> findByRaceId(@Param("raceId") Integer raceId);

    // Buscar personagens com transformação ativa
    @Query("SELECT c FROM Character c WHERE c.activeTransformation IS NOT NULL")
    List<Character> findWithActiveTransformation();
}
