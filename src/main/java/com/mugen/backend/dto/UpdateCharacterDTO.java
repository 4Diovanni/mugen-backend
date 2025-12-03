package com.mugen.backend.dto;

import jakarta.validation.constraints.Min;
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
public class UpdateCharacterDTO {

    @NotBlank(message = "Character name is required")
    @Size(min = 2, max = 120, message = "Character name must be between 2 and 120 characters")
    private String name;

    @Min(value = 1, message = "Level must be at least 1")
    private Integer level;

    @Min(value = 0, message = "Experience cannot be negative")
    private Long exp;

    @Min(value = 0, message = "TP cannot be negative")
    private Integer tp;

    private Boolean isActive;
}
