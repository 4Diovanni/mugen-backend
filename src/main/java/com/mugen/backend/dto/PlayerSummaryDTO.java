package com.mugen.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resumo de ‘player’
 * Usado em: GET /admin/‘players’
 * Campos:
 * - userId: ID único do ‘player’
 * - username: Nome de usuário
 * - email: Email do ‘player’
 * - createdAt: Data de criação da conta
 * - characterCount: Quantidade de personagens
 * - lastCharacterLevel: Nível máximo entre os personagens
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSummaryDTO {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("character_count")
    private Integer characterCount;

    @JsonProperty("last_character_level")
    private Integer lastCharacterLevel;
}
