package com.mugen.backend.dto.achievement;

import lombok.*;

/**
 * DTO para retornar resumo de achievements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterAchievementSummaryDTO {
    private Integer totalAchievements;      // Total de achievements no sistema
    private Integer unlockedAchievements;    // Quantos foram desbloqueados
    private Integer totalTpEarned;           // TP ganho com achievements
    private Long nextAchievementEstimatedTime; // Tempo estimado até próxima (em ms)
}
