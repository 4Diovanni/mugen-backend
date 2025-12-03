package com.mugen.backend.repository;

import com.mugen.backend.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CharacterRepository extends JpaRepository<Character, UUID> {

    // ========================================
    // CONSULTAS BÁSICAS (Spring Data Method Naming)
    // ========================================

    /**
     * Buscar todos os personagens de um usuário
     */
    List<Character> findByOwnerId(UUID ownerId);

    /**
     * Buscar todos os personagens de um usuário com paginação
     */
    Page<Character> findByOwnerId(UUID ownerId, Pageable pageable);

    /**
     * Buscar apenas personagens ativos de um usuário
     */
    List<Character> findByOwnerIdAndIsActiveTrue(UUID ownerId);

    /**
     * Verificar se existe um personagem com o mesmo nome para o mesmo usuário
     */
    boolean existsByOwnerIdAndName(UUID ownerId, String name);

    /**
     * Contar quantos personagens um usuário possui
     */
    long countByOwnerId(UUID ownerId);

    // ========================================
    // CONSULTAS COM FETCH JOINS (Queries Customizadas)
    // ========================================

    /**
     * Buscar personagem por ID carregando a raça
     */
    @Query("SELECT c FROM Character c " +
            "LEFT JOIN FETCH c.race " +
            "WHERE c.id = :id")
    Optional<Character> findByIdWithRace(@Param("id") UUID id);

    /**
     * Buscar personagem por ID carregando os atributos
     */
    @Query("SELECT c FROM Character c " +
            "LEFT JOIN FETCH c.attributes " +
            "WHERE c.id = :id")
    Optional<Character> findByIdWithAttributes(@Param("id") UUID id);

    /**
     * Buscar personagem por ID carregando TODOS os relacionamentos
     * Use com moderação - pode ser pesado em produção
     */
    @Query("SELECT DISTINCT c FROM Character c " +
            "LEFT JOIN FETCH c.owner " +
            "LEFT JOIN FETCH c.race " +
            "LEFT JOIN FETCH c.attributes " +
            "LEFT JOIN FETCH c.activeTransformation " +
            "LEFT JOIN FETCH c.skills " +
            "LEFT JOIN FETCH c.transformations " +
            "WHERE c.id = :id")
    Optional<Character> findByIdWithFullDetails(@Param("id") UUID id);

    /**
     * Listar todos os personagens com paginação carregando owner, race e attributes
     * Usado no endpoint GET /characters
     */
    @Query("SELECT DISTINCT c FROM Character c " +
            "LEFT JOIN FETCH c.owner " +
            "LEFT JOIN FETCH c.race " +
            "LEFT JOIN FETCH c.attributes")
    Page<Character> findAllWithDetails(Pageable pageable);

    // ========================================
    // CONSULTAS ESPECÍFICAS DE NEGÓCIO
    // ========================================

    /**
     * Buscar personagens por raça
     */
    @Query("SELECT c FROM Character c " +
            "LEFT JOIN FETCH c.owner " +
            "LEFT JOIN FETCH c.attributes " +
            "WHERE c.race.id = :raceId")
    List<Character> findByRaceId(@Param("raceId") Integer raceId);

    /**
     * Buscar personagens que possuem transformação ativa
     */
    @Query("SELECT c FROM Character c " +
            "LEFT JOIN FETCH c.activeTransformation " +
            "WHERE c.activeTransformation IS NOT NULL")
    List<Character> findWithActiveTransformation();

    /**
     * Buscar personagens ativos de um usuário (query customizada alternativa)
     * Mesma função que findByOwnerIdAndIsActiveTrue, mas usando @Query
     */
    @Query("SELECT c FROM Character c " +
            "WHERE c.owner.id = :ownerId " +
            "AND c.isActive = true")
    List<Character> findActiveByOwnerIdCustom(@Param("ownerId") UUID ownerId);
}
