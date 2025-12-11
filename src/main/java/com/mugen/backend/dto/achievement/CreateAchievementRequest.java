package com.mugen.backend.dto.achievement;

import lombok.*;

/**
 * DTO para criar/atualizar Achievement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAchievementRequest {
    private String keyName;      // Identificador único
    private String title;        // Título da conquista
    private String description;  // Descrição
    private String requirementJson; // JSON com requisitos
    private Integer rewardTp;    // TP de recompensa
    private String category;     // SPECIAL, COMBAT, EXPLORATION, etc
    private Boolean isActive;    // Ativo ou não
}