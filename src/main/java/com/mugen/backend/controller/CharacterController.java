package com.mugen.backend.controller;

import com.mugen.backend.dto.AllocateAttributeRequest;
import com.mugen.backend.dto.CharacterStats;
import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.User;
import com.mugen.backend.service.CharacterService;
import com.mugen.backend.service.CharacterStatService;
import com.mugen.backend.service.TPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
@Slf4j
public class CharacterController {

    private final CharacterService characterService;
    private final CharacterStatService statService;
    private final TPService tpService;

    /**
     * GET /characters
     * Listar todos os personagens (ou filtrar por owner)
     */
    @GetMapping
    public ResponseEntity<List<Character>> listCharacters(
            @RequestParam(required = false) UUID ownerId) {

        if (ownerId != null) {
            log.debug("Listing characters for owner: {}", ownerId);
            return ResponseEntity.ok(characterService.findByOwnerId(ownerId));
        }

        log.debug("Listing all characters");
        return ResponseEntity.ok(characterService.findAll());
    }

    /**
     * GET /characters/{id}
     * Buscar personagem por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacter(@PathVariable UUID id) {
        log.debug("Getting character: {}", id);

        return characterService.findByIdWithFullDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /characters/{id}/stats
     * Calcular stats finais do personagem
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CharacterStats> getCharacterStats(@PathVariable UUID id) {
        log.debug("Calculating stats for character: {}", id);

        return characterService.findByIdWithFullDetails(id)
                .map(statService::calculateStats)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /characters
     * Criar novo personagem
     */
    @PostMapping
    public ResponseEntity<Character> createCharacter(
            @RequestParam UUID ownerId,
            @RequestParam String name,
            @RequestParam Integer raceId) {

        log.info("Request to create character: {} for owner: {}", name, ownerId);  // ✅ Mudei para "Request to create"

        Character created = characterService.createCharacter(ownerId, name, raceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * POST /characters/{id}/allocate-attribute
     * Alocar pontos em atributo (gastar TP)
     */
    @PostMapping("/{id}/allocate-attribute")
    public ResponseEntity<Character> allocateAttribute(
            @PathVariable UUID id,
            @RequestBody @Valid AllocateAttributeRequest request) {

        log.info("Allocating {} points to {} for character {}",
                request.getPoints(), request.getAttributeName(), id);

        // TODO: Pegar user do contexto de segurança (quando implementar Spring Security)
        // Por enquanto, criar user fake
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        Character updated = tpService.allocateAttribute(id, request, mockUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /characters/{id}
     * Deletar personagem
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
        log.info("Deleting character: {}", id);

        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }
}
