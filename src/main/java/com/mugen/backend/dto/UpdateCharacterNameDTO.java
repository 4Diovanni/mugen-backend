package com.mugen.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCharacterNameDTO {

    @NotBlank(message = "Character name is required")
    @Size(min = 2, max = 120, message = "Character name must be between 2 and 120 characters")
    private String name;
}
