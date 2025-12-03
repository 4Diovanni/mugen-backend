package com.mugen.backend.controller;

import com.mugen.backend.dto.AllocateAttributeRequest;
import com.mugen.backend.dto.CharacterStats;
import com.mugen.backend.dto.UpdateCharacterDTO;
import com.mugen.backend.dto.UpdateCharacterNameDTO;
import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.User;
import com.mugen.backend.service.CharacterService;
import com.mugen.backend.service.CharacterStatService;
import com.mugen.backend.service.TPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * POST /characters
     * Criar novo personagem
     */
    @PostMapping
    public ResponseEntity<Character> createCharacter(
            @RequestParam UUID ownerId,
            @RequestParam String name,
            @RequestParam Integer raceId) {
        log.info("Request to create character: {} for owner: {}", name, ownerId);
        Character created = characterService.createCharacter(ownerId, name, raceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
     * GET /characters
     * Listar todos os personagens com paginação
     */
    @GetMapping
    public ResponseEntity<Page<Character>> getAllCharacters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.info("Request to get all characters - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, sortDirection);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Character> characters = characterService.getAllCharacters(pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}
     * Listar personagens de um usuário
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Character>> getCharactersByOwnerId(@PathVariable UUID ownerId) {
        log.info("Request to get characters by owner id: {}", ownerId);
        List<Character> characters = characterService.getCharactersByOwnerId(ownerId);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}/paginated
     * Listar personagens de um usuário com paginação
     */
    @GetMapping("/owner/{ownerId}/paginated")
    public ResponseEntity<Page<Character>> getCharactersByOwnerIdPaginated(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.info("Request to get paginated characters by owner id: {}", ownerId);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Character> characters = characterService.getCharactersByOwnerIdPaginated(ownerId, pageable);
        return ResponseEntity.ok(characters);
    }

    /**
     * GET /characters/owner/{ownerId}/count
     * Contar personagens de um usuário
     */
    @GetMapping("/owner/{ownerId}/count")
    public ResponseEntity<Long> countCharactersByOwnerId(@PathVariable UUID ownerId) {
        log.info("Request to count characters by owner id: {}", ownerId);
        long count = characterService.countCharactersByOwnerId(ownerId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /characters/{id}/exists
     * Verificar se personagem existe
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> characterExists(@PathVariable UUID id) {
        log.info("Request to check if character exists: {}", id);
        boolean exists = characterService.characterExists(id);
        return ResponseEntity.ok(exists);
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

//    /**
//     * DELETE /characters/{id}
//     * Deletar personagem
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
//        log.info("Deleting character: {}", id);
//        characterService.deleteCharacter(id);
//        return ResponseEntity.noContent().build();
//    }

    /**
     * PUT /characters/{id}
     * Atualizar personagem completo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterDTO dto) {
        log.info("Request to update character: {}", id);
        Character updated = characterService.updateCharacter(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /characters/{id}/name
     * Atualizar apenas o nome do personagem
     */
    @PatchMapping("/{id}/name")
    public ResponseEntity<Character> updateCharacterName(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCharacterNameDTO dto) {
        log.info("Request to update character name: {} to {}", id, dto.getName());
        Character updated = characterService.updateCharacterName(id, dto.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /characters/{id}
     * Deletar personagem (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable UUID id) {
        log.info("Request to delete character: {}", id);
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /characters/{id}/deactivate
     * Desativar personagem (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Character> deactivateCharacter(@PathVariable UUID id) {
        log.info("Request to deactivate character: {}", id);
        characterService.softDeleteCharacter(id);
        Character character = characterService.getCharacterById(id);
        return ResponseEntity.ok(character);
    }
}
