package com.mugen.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para retornar Achievement obtido por um character
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterAchievementDTO {
    private Long id;
    private Integer achievementId;
    private String achievementKeyName;
    private String achievementTitle;
    private String achievementDescription;
    private Integer rewardTp;
    private String category;
    private LocalDateTime obtainedAt;
    private String notificationMessage;
}
