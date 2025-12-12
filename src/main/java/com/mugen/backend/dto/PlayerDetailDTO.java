package com.mugen.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mugen.backend.dto.CharacterBasicDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para detalhes completos de um player
 * Usado em: GET /admin/players/{userId}
 * Campos:
 * - userId: ID único do player
 * - username: Nome de usuário
 * - email: Email do player
 * - createdAt: Data de criação da conta
 * - totalCharacters: Total de personagens do player
 * - characters: Lista de personagens com informações básicas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDetailDTO {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("total_characters")
    private Integer totalCharacters;

    @JsonProperty("characters")
    private List<CharacterBasicDTO> characters;
}
