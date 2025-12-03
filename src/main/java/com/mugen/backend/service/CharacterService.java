package com.mugen.backend.service;

import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.CharacterAttribute;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.User;
import com.mugen.backend.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;
    private final RaceService raceService;

    private static final int MAX_CHARACTERS_PER_USER = 5;
    private static final int STARTING_TP = 10;

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
        // ✅ CORRIGIDO - método existe no repository
        return characterRepository.findByOwnerIdAndIsActiveTrue(ownerId);
    }

    public Optional<Character> findById(UUID id) {
        log.debug("Finding character by id: {}", id);
        return characterRepository.findById(id);
    }

    public Optional<Character> findByIdWithRace(UUID id) {
        log.debug("Finding character by id with race: {}", id);
        // ✅ CORRIGIDO - método existe no repository
        return characterRepository.findByIdWithRace(id);
    }

    public Optional<Character> findByIdWithAttributes(UUID id) {
        log.debug("Finding character by id with attributes: {}", id);
        return characterRepository.findByIdWithAttributes(id);
    }

    // ✅ ADICIONE ESTE MÉTODO NOVO
    public Optional<Character> findByIdWithFullDetails(UUID id) {
        log.debug("Finding character by id with full details: {}", id);
        return characterRepository.findByIdWithFullDetails(id);
    }

    @Transactional
    public Character createCharacter(UUID ownerId, String name, Integer raceId) {
        log.info("Creating character: {} for owner: {}", name, ownerId);  // ✅ Mantém "Creating"

        // ✅ VALIDAÇÃO 1: Limite de personagens
        long characterCount = characterRepository.countByOwnerId(ownerId);
        if (characterCount >= MAX_CHARACTERS_PER_USER) {
            throw new IllegalStateException("User has reached maximum character limit: " + MAX_CHARACTERS_PER_USER);
        }

        // ✅ VALIDAÇÃO 2: Nome único por usuário (EVITA DUPLICAÇÃO!)
        if (characterRepository.existsByOwnerIdAndName(ownerId, name)) {
            throw new IllegalArgumentException("Character name already exists for this user: " + name);
        }

        // Buscar owner e race
        User owner = userService.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownerId));

        Race race = raceService.findById(raceId)
                .orElseThrow(() -> new IllegalArgumentException("Race not found: " + raceId));

        if (!race.getIsActive()) {
            throw new IllegalArgumentException("Race is not active: " + race.getName());
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

        // Criar atributos
        CharacterAttribute attributes = CharacterAttribute.builder()
                .str(race.getStartStr())
                .dex(race.getStartDex())
                .con(race.getStartCon())
                .wil(race.getStartWil())
                .mnd(race.getStartMnd())
                .spi(race.getStartSpi())
                .build();

        // Definir relacionamento bidirecional
        character.setAttributes(attributes);

        // Salvar
        Character saved = characterRepository.save(character);

        log.info("Created character: {} for user: {}", saved.getName(), owner.getEmail());

        return saved;
    }


    @Transactional
    public Character updateCharacter(Character character) {
        log.info("Updating character: {}", character.getName());
        return characterRepository.save(character);
    }

    @Transactional
    public void deleteCharacter(UUID characterId) {
        log.info("Deleting character: {}", characterId);
        characterRepository.deleteById(characterId);
    }

    public boolean isOwner(UUID characterId, UUID userId) {
        return characterRepository.findById(characterId)
                .map(character -> character.getOwner().getId().equals(userId))
                .orElse(false);
    }

    public long countByOwnerId(UUID ownerId) {
        return characterRepository.countByOwnerId(ownerId);
    }
}
