package com.mugen.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para estatísticas administrativas
 * Usado em: GET /api/v1/admin/players/stats/summary
 *
 * Campos:
 * - totalPlayers: Total de jogadores cadastrados
 * - activePlayers: Players com pelo menos 1 personagem
 * - totalCharacters: Total de personagens do jogo
 * - averageCharactersPerPlayer: Média de personagens por player
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {

    @JsonProperty("total_players")
    private Integer totalPlayers;

    @JsonProperty("active_players")
    private Integer activePlayers;

    @JsonProperty("total_characters")
    private Integer totalCharacters;

    @JsonProperty("average_characters_per_player")
    private Integer averageCharactersPerPlayer;
}
