package com.mugen.backend.service;

import com.mugen.backend.entity.*;
import com.mugen.backend.entity.Character;
import com.mugen.backend.exception.CharacterNotFoundException;
import com.mugen.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransformationService {

    private final CharacterRepository characterRepository;
    private final CharacterTransformationRepository characterTransformationRepository;
    private final TransformationRepository transformationRepository;

    /**
     * Desbloquear transformação para personagem
     */
    @Transactional
    public CharacterTransformation unlockTransformation(UUID characterId, Integer transformationId) {
        log.info("Unlocking transformation {} for character {}", transformationId, characterId);

        // 1️⃣ Validar se personagem existe
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException(characterId));

        // 2️⃣ Validar se transformação existe
        Transformation transformation = transformationRepository.findById(transformationId)
                .orElseThrow(() -> new RuntimeException("Transformation not found with id: " + transformationId));

        // 3️⃣ Validar se personagem já desbloqueou
        // ✅ CORRIGIDO: Usar CharacterTransformationId (classe separada, não inner class)
        CharacterTransformationId id = new CharacterTransformationId(characterId, transformationId);

        CharacterTransformation existing = characterTransformationRepository.findById(id)
                .orElse(null);

        if (existing != null && existing.getUnlocked()) {
            throw new IllegalStateException("Character already unlocked this transformation");
        }

        // 4️⃣ Validar requisitos
        if (transformation.getRequiredLevel() != null &&
                character.getLevel() < transformation.getRequiredLevel()) {
            throw new IllegalStateException(
                    "Character level " + character.getLevel() +
                            " is lower than required level " + transformation.getRequiredLevel()
            );
        }

        if (transformation.getRaceRequired() && transformation.getRace() != null &&
                !character.getRace().getId().equals(transformation.getRace().getId())) {
            throw new IllegalStateException(
                    "Transformation requires race: " + transformation.getRace().getName()
            );
        }

        // 5️⃣ Criar ou atualizar CharacterTransformation
        CharacterTransformation charTransformation;
        if (existing != null) {
            charTransformation = existing;
            charTransformation.unlock();
        } else {
            charTransformation = CharacterTransformation.builder()
                    .id(id)  // ✅ Passou corretamente agora
                    .character(character)
                    .transformation(transformation)
                    .unlocked(true)
                    .build();
            charTransformation.unlock();
        }

        // 6️⃣ Salvar
        CharacterTransformation saved = characterTransformationRepository.save(charTransformation);
        log.info("Transformation {} unlocked for character {}", transformationId, characterId);

        return saved;
    }

    /**
     * Listar todas as transformações do personagem
     */
    @Transactional(readOnly = true)
    public List<CharacterTransformation> getCharacterTransformations(UUID characterId) {
        log.info("Getting transformations for character {}", characterId);

        if (!characterRepository.existsById(characterId)) {
            throw new CharacterNotFoundException(characterId);
        }

        return characterTransformationRepository.findByCharacterId(characterId);
    }

    /**
     * Listar apenas transformações desbloqueadas
     */
    @Transactional(readOnly = true)
    public List<CharacterTransformation> getUnlockedTransformations(UUID characterId) {
        log.info("Getting unlocked transformations for character {}", characterId);

        if (!characterRepository.existsById(characterId)) {
            throw new CharacterNotFoundException(characterId);
        }

        return characterTransformationRepository.findUnlockedByCharacterId(characterId);
    }

    /**
     * Verificar se personagem tem transformação específica desbloqueada
     */
    @Transactional(readOnly = true)
    public boolean hasUnlockedTransformation(UUID characterId, Integer transformationId) {
        log.debug("Checking if character {} has unlocked transformation {}", characterId, transformationId);
        return characterTransformationRepository
                .findUnlockedTransformation(characterId, transformationId)
                .isPresent();
    }

    /**
     * Listar transformações disponíveis para um personagem
     */
    @Transactional(readOnly = true)
    public List<Transformation> getAvailableTransformations(UUID characterId) {
        log.info("Getting available transformations for character {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException(characterId));

        return transformationRepository.findAvailableForCharacter(
                character.getLevel(),
                character.getRace().getId()
        );
    }
}