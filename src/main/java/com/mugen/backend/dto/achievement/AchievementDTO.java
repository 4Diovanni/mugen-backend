package com.mugen.backend.dto.achievement;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para retornar Achievement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDTO {
    private Integer id;
    private String keyName;
    private String title;
    private String description;
    private String requirementJson;
    private Integer rewardTp;
    private String category;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
