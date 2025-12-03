package com.mugen.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardTPRequest {

    @NotNull(message = "Character ID is required")
    private UUID characterId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Must award at least 1 TP")
    @Max(value = 1000, message = "Cannot award more than 1000 TP at once")
    private Integer amount;

    @NotBlank(message = "Reason is required")
    private String reason; // "MINIGAME_REFLEX", "MASTER_REWARD", "EVENT_COMPLETION", etc.
}
