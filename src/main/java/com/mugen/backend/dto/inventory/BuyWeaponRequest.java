package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para comprar arma
 * 
 * Usado pelo endpoint de compra de arma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyWeaponRequest {
    
    @NotNull(message = "ID da arma é obrigatório")
    private Integer weaponId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantity;
}
