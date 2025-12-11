package com.mugen.backend.dto.tp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ganhar experiência
 * Exemplo:
 * {
 *   "amount": 150,
 *   "reason": "QUEST_COMPLETED",
 *   "source": "Dragon Slayer Quest"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GainExpRequest {
    
    @Positive(message = "Experience amount must be positive")
    private Long amount;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String source; // Opcional: descreve a fonte (quest, mob, etc)
}
