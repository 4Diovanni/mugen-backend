package com.mugen.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para informações básicas de um personagem
 * Usado em: PlayerDetailDTO (lista de personagens)
 * Campos:
 * - id: ID do personagem
 * - name: Nome do personagem
 * - race: Nome da raça
 * - level: Nível atual
 * - experience: Experiência acumulada
 * - createdAt: Data de criação do personagem
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterBasicDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("race")
    private String race;

    @JsonProperty("level")
    private Integer level;

    @JsonProperty("experience")
    private Long experience;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
