package com.mugen.backend.controller;

import com.mugen.backend.dto.AdminStatsDTO;
import com.mugen.backend.dto.CharacterBasicDTO;
import com.mugen.backend.dto.PlayerDetailDTO;
import com.mugen.backend.dto.PlayerSummaryDTO;
import com.mugen.backend.entity.User;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.exception.ResourceNotFoundException;
import com.mugen.backend.service.CharacterService;
import com.mugen.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/players")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final CharacterService characterService;

    /**
     * Listar todos os players com informações básicas (ADMIN only)
     * GET /admin/players
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlayerSummaryDTO>> getAllPlayers() {
        log.info("Fetching all players for admin panel");

        try {
            List<User> users = userService.findAllPlayers();

            List<PlayerSummaryDTO> playerSummaries = users.stream()
                    .map(this::convertToPlayerSummary)
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} players", playerSummaries.size());
            return ResponseEntity.ok(playerSummaries);
        } catch (Exception e) {
            log.error("Error fetching players", e);
            throw e;
        }
    }

    /**
     * Obter detalhes completos de um player específico (ADMIN only)
     * GET /api/v1/admin/players/{userId}
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerDetailDTO> getPlayerDetail(@PathVariable UUID userId) {
        log.info("Fetching player details for userId: {}", userId);

        return userService.findById(userId)
                .map(user -> {
                    log.info("Player found: {}", user.getDisplayName());
                    List<Character> characters = characterService.findByUserId(userId);
                    return ResponseEntity.ok(convertToPlayerDetail(user, characters));
                })
                .orElseThrow(() -> {
                    log.warn("Player not found with id: {}", userId);
                    return new ResourceNotFoundException("Jogador não encontrado com ID: " + userId);
                });
    }

    /**
     * Resumo estatístico de todos os players (ADMIN only)
     * GET /api/v1/admin/players/stats/summary
     */
    @GetMapping("/stats/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDTO> getAdminStats() {
        log.info("Fetching admin statistics");

        try {
            List<User> allUsers = userService.findAllPlayers();

            int totalPlayers = allUsers.size();
            int totalCharacters = Math.toIntExact(characterService.countAll());
            int activePlayers = (int) allUsers.stream()
                    .filter(user -> !characterService.findByUserId(user.getId()).isEmpty())
                    .count();

            AdminStatsDTO stats = AdminStatsDTO.builder()
                    .totalPlayers(totalPlayers)
                    .activePlayers(activePlayers)
                    .totalCharacters(totalCharacters)
                    .averageCharactersPerPlayer(totalPlayers > 0 ? totalCharacters / totalPlayers : 0)
                    .build();

            log.info("Admin stats: {} total players, {} characters", totalPlayers, totalCharacters);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching admin stats", e);
            throw e;
        }
    }

    /**
     * Converter User para PlayerSummaryDTO
     */
    private PlayerSummaryDTO convertToPlayerSummary(User user) {
        List<Character> characters = characterService.findByUserId(user.getId());

        return PlayerSummaryDTO.builder()
                .userId(user.getId())
                .username(user.getDisplayName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .characterCount(characters.size())
                .lastCharacterLevel(characters.stream()
                        .mapToInt(Character::getLevel)
                        .max()
                        .orElse(0))
                .build();
    }

    /**
     * Converter User e Characters para PlayerDetailDTO
     */
    private PlayerDetailDTO convertToPlayerDetail(User user, List<Character> characters) {
        List<CharacterBasicDTO> characterDTOs = characters.stream()
                .map(character -> CharacterBasicDTO.builder()
                        .id(character.getId())
                        .name(character.getName())
                        .race(character.getRace().getName())
                        .level(character.getLevel())
                        .experience(character.getExp())
                        .createdAt(character.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PlayerDetailDTO.builder()
                .userId(user.getId())
                .username(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .totalCharacters(characters.size())
                .characters(characterDTOs)
                .build();
    }
}