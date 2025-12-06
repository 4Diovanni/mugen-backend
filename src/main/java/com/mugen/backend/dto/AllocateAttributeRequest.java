package com.mugen.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para alocar pontos em atributo
 * POST /characters/{id}/allocate-attribute
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateAttributeRequest {

    @NotBlank(message = "Nome do atributo é obrigatório")
    @Pattern(
            regexp = "^(STR|DEX|CON|WIL|MND|SPI)$",
            message = "Atributo inválido. Válidos: STR, DEX, CON, WIL, MND, SPI"
    )
    private String attributeName;

    @NotNull(message = "Pontos é obrigatório")
    @Min(value = 1, message = "Mínimo 1 ponto")
    @Max(value = 50, message = "Máximo 50 pontos por alocação")
    private Integer points;
}
