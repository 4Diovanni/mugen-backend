package com.mugen.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TPSummary {

    private Integer currentTP;
    private Long totalEarned;
    private Long totalSpent;
    private Long lifetimeTP; // totalEarned

    // Breakdown por categoria
    private Long earnedFromMinigames;
    private Long earnedFromMaster;
    private Long earnedFromEvents;

    private Long spentOnAttributes;
    private Long spentOnSkills;
    private Long spentOnTransformations;
}
