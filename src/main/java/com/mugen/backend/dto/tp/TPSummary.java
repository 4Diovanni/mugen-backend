package com.mugen.backend.dto.tp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO para resposta de TP Summary (resumo de TP do personagem)
 * GET /characters/{id}/tp-summary
 * Mostra:
 * - TP atual
 * - TP total ganhado na vida
 * - TP total gasto
 * - Breakdown por categoria
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TPSummary {

    private Integer currentTP;
    private Long totalEarned;
    private Long totalSpent;
    private Long lifetimeTP;

    // Ganhos por categoria
    private Long earnedFromMinigames;
    private Long earnedFromMaster;
    private Long earnedFromEvents;

    // Gastos por categoria
    private Long spentOnAttributes;
    private Long spentOnSkills;
    private Long spentOnTransformations;

    /**
     * Calcula TP restante disponível
     */
    public Integer getAvailableTP() {
        return currentTP;
    }

    /**
     * Calcula taxa de conversão (quanto % do TP ganho foi gasto)
     */
    public Double getUsageRate() {
        if (totalEarned == null || totalEarned == 0) {
            return 0.0;
        }
        return (double) totalSpent / totalEarned * 100;
    }
}
