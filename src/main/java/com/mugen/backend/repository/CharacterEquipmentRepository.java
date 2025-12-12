package com.mugen.backend.repository;

import com.mugen.backend.entity.character.CharacterEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para CharacterEquipment
 * Gerencia equipamento ativo do personagem
 */
@Repository
public interface CharacterEquipmentRepository extends JpaRepository<CharacterEquipment, Long> {
    
    // ==================== BUSCA BÁSICA ====================
    
    /**
     * Busca equipamento do personagem
     */
    Optional<CharacterEquipment> findByCharacterId(UUID characterId);
    
    /**
     * Verifica se personagem tem equipamento
     */
    boolean existsByCharacterId(UUID characterId);

    // ==================== BUSCAS CUSTOMIZADAS ====================
    
    /**
     * Lista personagens com arma equipada
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.weapon IS NOT NULL " +
           "AND ce.status = 'EQUIPADO'")
    List<CharacterEquipment> findCharactersWithWeaponEquipped();
    
    /**
     * Lista personagens com armadura equipada
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.armor IS NOT NULL " +
           "AND ce.status = 'EQUIPADO'")
    List<CharacterEquipment> findCharactersWithArmorEquipped();
    
    /**
     * Lista personagens com ambos equipados
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.weapon IS NOT NULL " +
           "AND ce.armor IS NOT NULL AND ce.status = 'EQUIPADO'")
    List<CharacterEquipment> findCharactersFullyEquipped();
    
    /**
     * Lista personagens desarmados (sem arma)
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.weapon IS NULL")
    List<CharacterEquipment> findCharactersDisarmed();
    
    /**
     * Lista personagens desprotegidos (sem armadura)
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.armor IS NULL")
    List<CharacterEquipment> findCharactersUnprotected();
    
    /**
     * Busca equipamento ativo por ID do personagem
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.characterId = :characterId " +
           "AND ce.status = 'EQUIPADO'")
    Optional<CharacterEquipment> findActiveEquipmentByCharacter(@Param("characterId") UUID characterId);
    
    /**
     * Busca equipamento inativo por ID do personagem
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.characterId = :characterId " +
           "AND ce.status = 'NAO_EQUIPADO'")
    Optional<CharacterEquipment> findInactiveEquipmentByCharacter(@Param("characterId") UUID characterId);
    
    /**
     * Calcula bônus total de STR do equipamento
     */
    @Query("SELECT COALESCE(ce.weapon.strBonus, 0) + COALESCE(ce.armor.strBonus, 0) " +
           "FROM CharacterEquipment ce WHERE ce.characterId = :characterId AND ce.status = 'EQUIPADO'")
    Integer calculateTotalStrBonus(@Param("characterId") UUID characterId);
    
    /**
     * Calcula bônus total de DEX do equipamento
     */
    @Query("SELECT COALESCE(ce.weapon.dexBonus, 0) + COALESCE(ce.armor.dexBonus, 0) " +
           "FROM CharacterEquipment ce WHERE ce.characterId = :characterId AND ce.status = 'EQUIPADO'")
    Integer calculateTotalDexBonus(@Param("characterId") UUID characterId);
    
    /**
     * Calcula bônus total de CON do equipamento
     */
    @Query("SELECT COALESCE(ce.weapon.conBonus, 0) + COALESCE(ce.armor.conBonus, 0) " +
           "FROM CharacterEquipment ce WHERE ce.characterId = :characterId AND ce.status = 'EQUIPADO'")
    Integer calculateTotalConBonus(@Param("characterId") UUID characterId);
    
    /**
     * Busca personagens com arma lendária equipada
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.weapon IS NOT NULL " +
           "AND ce.weapon.rarity = 'LENDARIO' AND ce.status = 'EQUIPADO'")
    List<CharacterEquipment> findCharactersWithLegendaryWeapon();
    
    /**
     * Busca personagens com armadura lendária equipada
     */
    @Query("SELECT ce FROM CharacterEquipment ce WHERE ce.armor IS NOT NULL " +
           "AND ce.armor.rarity = 'LENDARIO' AND ce.status = 'EQUIPADO'")
    List<CharacterEquipment> findCharactersWithLegendaryArmor();
    
    /**
     * Conta personagens com equipamento completo
     */
    @Query("SELECT COUNT(ce) FROM CharacterEquipment ce WHERE ce.weapon IS NOT NULL " +
           "AND ce.armor IS NOT NULL AND ce.status = 'EQUIPADO'")
    long countFullyEquippedCharacters();
}
