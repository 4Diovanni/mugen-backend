package com.mugen.backend.service;

import com.mugen.backend.dto.character.CharacterDTO;
import com.mugen.backend.dto.character.UpdateCharacterDTO;
import com.mugen.backend.entity.*;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.entity.character.CharacterSkill;
import com.mugen.backend.exception.CharacterNotFoundException;
import com.mugen.backend.repository.*;
import com.mugen.backend.repository.skills.CharacterSkillRepository;
import com.mugen.backend.repository.skills.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mugen.backend.entity.character.CharacterSkillId;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CharacterService {

    private final SkillRepository skillRepository;
    private final CharacterSkillRepository characterSkillRepository;
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;

    private static final int MAX_CHARACTERS_PER_USER = 5;
    private static final int STARTING_TP = 10;

    // ========== MÉTODOS ANTIGOS (mantidos para compatibilidade) ==========

    public List<Character> findAll() {
        log.debug("Finding all characters");
        return characterRepository.findAll();
    }

    public List<Character> findByOwnerId(UUID ownerId) {
        log.debug("Finding characters by owner id: {}", ownerId);
        return characterRepository.findByOwnerId(ownerId);
    }

    public List<Character> findActiveByOwnerId(UUID ownerId) {
        log.debug("Finding active characters by owner id: {}", ownerId);
        return characterRepository.findByOwnerIdAndIsActiveTrue(ownerId);
    }

    public Optional<Character> findById(UUID id) {
        log.debug("Finding character by id: {}", id);
        return characterRepository.findById(id);
    }

    public Optional<Character> findByIdWithRace(UUID id) {
        log.debug("Finding character by id with race: {}", id);
        return characterRepository.findByIdWithRace(id);
    }

    public Optional<Character> findByIdWithAttributes(UUID id) {
        log.debug("Finding character by id with attributes: {}", id);
        return characterRepository.findByIdWithAttributes(id);
    }

    public Optional<Character> findByIdWithFullDetails(UUID id) {
        log.debug("Finding character by id with full details: {}", id);
        return characterRepository.findByIdWithFullDetails(id);
    }

    // ========== CRIAÇÃO DE PERSONAGEM ==========

    @Transactional
    public Character createCharacter(CharacterDTO dto) {
        log.info("Request to create character: {}", dto.getName());

        // Validar se já existe um personagem com este nome para este usuário
        if (characterRepository.existsByOwnerIdAndName(dto.getOwnerId(), dto.getName())) {
            throw new IllegalArgumentException("Personagem com este nome já existe para este usuário");
        }

        // Buscar proprietário
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Buscar raça
        Race race = raceRepository.findById(dto.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Raça não encontrada"));

        log.info("Creating character: {} for owner: {}", dto.getName(), owner.getId());

        // Criar personagem
        Character character = Character.builder()
                .name(dto.getName())
                .owner(owner)
                .race(race)
                .level(1)
                .exp(0L)
                .tp(0)
                .isActive(true)
                .build();

        // ⚠️ CRÍTICO: Apenas cria Character
        // CharacterAttribute será criado AUTOMATICAMENTE pela Race via JPA cascade
        Character saved = characterRepository.save(character);

        log.info("Character created successfully with id: {}", saved.getId());
        return saved;
    }

    // ========== NOVOS MÉTODOS DE CONSULTA ==========

    /**
     * Buscar todos os personagens (sem paginação)
     * Usado principalmente em testes e casos simples
     */
    @Transactional(readOnly = true)
    public List<Character> getAllCharacters() {
        log.info("Finding all characters without pagination");
        return characterRepository.findAll();
    }

    /**
     * Buscar todos os personagens com paginação
     * Usado no endpoint GET /api/characters
     */
    @Transactional(readOnly = true)
    public Page<Character> getAllCharacters(Pageable pageable) {
        log.info("Finding all characters with pagination");
        return characterRepository.findAllWithDetails(pageable);
    }

    @Transactional(readOnly = true)
    public Character getCharacterById(UUID id) {
        log.info("Finding character by id: {}", id);
        return characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Character> getCharactersByOwner(UUID ownerId) {
        log.info("Finding characters by owner id: {}", ownerId);

        // Validar se o usuário existe
        if (!userRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("User not found with id: " + ownerId);
        }

        return characterRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public Page<Character> getCharactersByOwnerPaginated(UUID ownerId, Pageable pageable) {
        log.info("Finding characters by owner id: {} with pagination", ownerId);

        // Validar se o usuário existe
        if (!userRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("User not found with id: " + ownerId);
        }

        return characterRepository.findByOwnerId(ownerId, pageable);
    }

    @Transactional(readOnly = true)
    public long countCharactersByOwner(UUID ownerId) {
        log.info("Counting characters for owner: {}", ownerId);
        return characterRepository.countByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public boolean characterExists(UUID id) {
        return characterRepository.existsById(id);
    }

    // ========== ATUALIZAÇÃO ==========

    @Transactional
    public Character updateCharacter(UUID id, UpdateCharacterDTO dto) {
        log.info("Updating character: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        // Validar nome único se estiver mudando o nome
        if (dto.getName() != null && !dto.getName().equals(character.getName())) {
            if (characterRepository.existsByOwnerIdAndName(character.getOwner().getId(), dto.getName())) {
                throw new IllegalStateException("User already has a character with name: " + dto.getName());
            }
            character.setName(dto.getName());
        }

        // Atualizar campos opcionais
        if (dto.getLevel() != null) {
            character.setLevel(dto.getLevel());
        }
        if (dto.getExp() != null) {
            character.setExp(dto.getExp());
        }
        if (dto.getTp() != null) {
            character.setTp(dto.getTp());
        }
        if (dto.getIsActive() != null) {
            character.setIsActive(dto.getIsActive());
        }

        Character updated = characterRepository.save(character);
        log.info("Updated character {} with id {}", updated.getName(), updated.getId());

        return updated;
    }

    @Transactional
    public Character updateCharacterName(UUID id, String newName) {
        log.info("Updating character name: {} to {}", id, newName);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        // Validar nome único
        if (characterRepository.existsByOwnerIdAndName(character.getOwner().getId(), newName)) {
            throw new IllegalStateException("User already has a character with name: " + newName);
        }

        character.setName(newName);
        Character updated = characterRepository.save(character);

        log.info("Character name updated to {}", newName);
        return updated;
    }

    // ========== DELEÇÃO ==========

    @Transactional
    public void deleteCharacter(UUID id) {
        log.info("Deleting character: {}", id);

        if (!characterRepository.existsById(id)) {
            throw new CharacterNotFoundException(id);
        }

        characterRepository.deleteById(id);
        log.info("Character deleted successfully: {}", id);
    }

    @Transactional
    public void softDeleteCharacter(UUID id) {
        log.info("Soft deleting character: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));

        character.setIsActive(false);
        characterRepository.save(character);

        log.info("Character soft deleted: {}", id);
    }

    // ========== UTILITÁRIOS ==========

    public boolean isOwner(UUID characterId, UUID userId) {
        return characterRepository.findById(characterId)
                .map(character -> character.getOwner().getId().equals(userId))
                .orElse(false);
    }

    public long countByOwnerId(UUID ownerId) {
        return characterRepository.countByOwnerId(ownerId);
    }

    @Transactional
    public Character updateCharacter(Character character) {
        log.info("Updating character: {}", character.getName());
        return characterRepository.save(character);
    }

    // ========== SKILLS ==========

    /**
     * Adicionar skill ao personagem
     * ✅ CORRIGIDO: Usar CharacterSkillId (standalone class) ao invés de CharacterSkill.CharacterSkillId
     */
    @Transactional
    public CharacterSkill addSkillToCharacter(UUID characterId, Integer skillId) {
        log.info("Adding skill {} to character {}", skillId, characterId);

        // 1️⃣ Validar se personagem existe
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException(characterId));

        // 2️⃣ Validar se skill existe
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));

        // 3️⃣ Verificar se personagem já tem essa skill
        CharacterSkillId id = new CharacterSkillId(characterId, skillId);
        if (characterSkillRepository.existsById(id)) {
            throw new IllegalStateException("Character already has this skill");
        }

        // 4️⃣ Criar CharacterSkill com as associações
        CharacterSkill characterSkill = CharacterSkill.builder()
                .id(id)  // ✅ Usar CharacterSkillId (standalone class)
                .character(character)
                .skill(skill)
                .currentLevel(1)
                .build();

        // 5️⃣ Salvar
        CharacterSkill saved = characterSkillRepository.save(characterSkill);
        log.info("Skill {} added to character {}", skillId, characterId);

        return saved;
    }

    /**
     * Listar skills do personagem
     */
    @Transactional(readOnly = true)
    public List<CharacterSkill> getCharacterSkills(UUID characterId) {
        log.info("Getting skills for character {}", characterId);

        // Validar se personagem existe
        if (!characterRepository.existsById(characterId)) {
            throw new CharacterNotFoundException(characterId);
        }

        return characterSkillRepository.findByCharacterId(characterId);
    }

    /**
     * Remover skill do personagem
     */
    @Transactional
    public void removeSkillFromCharacter(UUID characterId, Integer skillId) {
        log.info("Removing skill {} from character {}", skillId, characterId);

        CharacterSkillId id = new CharacterSkillId(characterId, skillId);

        if (!characterSkillRepository.existsById(id)) {
            throw new RuntimeException("CharacterSkill not found");
        }

        characterSkillRepository.deleteById(id);
        log.info("Skill {} removed from character {}", skillId, characterId);
    }
}
