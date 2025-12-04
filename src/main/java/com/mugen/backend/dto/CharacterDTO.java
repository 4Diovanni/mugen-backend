package com.mugen.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    @NotBlank(message = "Character name is required")
    @Size(min = 2, max = 120, message = "Character name must be between 2 and 120 characters")
    private String name;

    @NotNull(message = "Race ID is required")
    private Integer raceId;
}
