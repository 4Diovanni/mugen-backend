package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para comprar armadura
 * Usado pelo endpoint de compra de armadura
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyArmorRequest {
    
    @NotNull(message = "ID da armadura é obrigatório")
    private Integer armorId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantity;
}
