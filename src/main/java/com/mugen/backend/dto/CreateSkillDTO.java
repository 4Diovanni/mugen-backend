package com.mugen.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSkillDTO {

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 150, message = "Skill name must be between 2 and 150 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Skill type is required")
    @Pattern(
            regexp = "ACTIVE|PASSIVE|RACIAL|ULTIMATE",
            message = "Invalid skill type. Must be: ACTIVE, PASSIVE, RACIAL, or ULTIMATE"
    )  // ✅ CORRIGIDO
    private String skillType;

    @Min(value = 0, message = "Base TP cost cannot be negative")
    private Integer baseTpCost;

    @Min(value = 1, message = "Max level must be at least 1")
    @Max(value = 100, message = "Max level cannot exceed 100")
    private Integer maxLevel;

    @Min(value = 1, message = "Required level must be at least 1")  // ✅ ADICIONAR
    private Integer requiredLevel;

    private Boolean isActive = true;
}
