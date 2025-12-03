package com.mugen.backend.service;

import com.mugen.backend.dto.UpdateCharacterDTO;
import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.CharacterAttribute;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.User;
import com.mugen.backend.exception.CharacterNotFoundException;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.RaceRepository;
import com.mugen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CharacterService {

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
    public Character createCharacter(UUID ownerId, String name, Integer raceId) {
        log.info("Request to create character {} for owner {}", name, ownerId);

        // Validar se o usuário existe
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + ownerId));

        // Validar se a raça existe
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new IllegalArgumentException("Race not found with id: " + raceId));

        // Validar limite de personagens por usuário
        long characterCount = characterRepository.countByOwnerId(ownerId);
        if (characterCount >= MAX_CHARACTERS_PER_USER) {
            throw new IllegalStateException("User already has the maximum number of characters (" + MAX_CHARACTERS_PER_USER + ")");
        }

        // Validar nome único por usuário
        if (characterRepository.existsByOwnerIdAndName(ownerId, name)) {
            throw new IllegalStateException("User already has a character with name: " + name);
        }

        // Criar personagem
        Character character = Character.builder()
                .owner(owner)
                .name(name)
                .race(race)
                .level(1)
                .exp(0L)
                .tp(STARTING_TP)
                .isActive(true)
                .build();

        // Criar atributos baseados na raça
        CharacterAttribute attributes = CharacterAttribute.builder()
                .character(character)
                .str(race.getStartStr())
                .dex(race.getStartDex())
                .con(race.getStartCon())
                .wil(race.getStartWil())
                .mnd(race.getStartMnd())
                .spi(race.getStartSpi())
                .build();

        character.setAttributes(attributes);

        Character savedCharacter = characterRepository.save(character);
        log.info("Created character {} with id {}", savedCharacter.getName(), savedCharacter.getId());

        return savedCharacter;
    }

    // ========== NOVOS MÉTODOS DE CONSULTA ==========

    @Transactional(readOnly = true)
    public Character getCharacterById(UUID id) {
        log.info("Finding character by id: {}", id);
        return characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<Character> getAllCharacters(Pageable pageable) {
        log.info("Finding all characters with pagination");
        return characterRepository.findAllWithDetails(pageable); // ✅ Usar query com JOIN FETCH
    }

    @Transactional(readOnly = true)
    public List<Character> getCharactersByOwnerId(UUID ownerId) {
        log.info("Finding characters by owner id: {}", ownerId);

        // Validar se o usuário existe
        if (!userRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("User not found with id: " + ownerId);
        }

        return characterRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public Page<Character> getCharactersByOwnerIdPaginated(UUID ownerId, Pageable pageable) {
        log.info("Finding characters by owner id: {} with pagination", ownerId);

        // Validar se o usuário existe
        if (!userRepository.existsById(ownerId)) {
            throw new IllegalArgumentException("User not found with id: " + ownerId);
        }

        return characterRepository.findByOwnerId(ownerId, pageable);
    }

    @Transactional(readOnly = true)
    public long countCharactersByOwnerId(UUID ownerId) {
        log.info("Counting characters for owner: {}", ownerId);
        return characterRepository.countByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public boolean characterExists(UUID id) {
        return characterRepository.existsById(id);
    }



    // ========== ATUALIZAÇÃO E DELEÇÃO ==========

    @Transactional
    public Character updateCharacter(Character character) {
        log.info("Updating character: {}", character.getName());
        return characterRepository.save(character);
    }

//    @Transactional
//    public void deleteCharacter(UUID characterId) {
//        log.info("Deleting character: {}", characterId);
//        characterRepository.deleteById(characterId);
//    }

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
}
