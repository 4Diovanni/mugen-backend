package com.mugen.backend.dto;

import jakarta.validation.constraints.*;
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

    @NotNull(message = "Character ID é obrigatório")
    private UUID characterId;

    @NotNull(message = "Quantidade de TP é obrigatória")
    @Min(value = 1, message = "Mínimo 1 TP")
    @Max(value = 1000, message = "Máximo 1000 TP por transação")
    private Integer amount;

    @NotBlank(message = "Motivo é obrigatório")
    @Size(min = 3, max = 100, message = "Motivo deve ter entre 3 e 100 caracteres")
    private String reason;
}
