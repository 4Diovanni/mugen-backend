package com.mugen.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceInfo {

    private int currentLevel;
    private long currentExp;
    private long expForNextLevel;
    private double progressPercentage;
    private long totalExpEarned;
    private boolean isMaxLevel;
    private long nextLevelIn;
}
