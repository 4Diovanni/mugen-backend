package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para vender item do inventário
 * Usado pelo endpoint de venda de item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellItemRequest {
    
    @NotNull(message = "ID do item no inventário é obrigatório")
    private Integer inventoryItemId;
    
    @NotNull(message = "Tipo do item é obrigatório")
    private String itemType;           // WEAPON, ARMOR, MATERIAL
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantity;

    // ✅ NOVO: Adicionar getter para compatibilidade com controller
    public Integer getItemId() {
        return this.inventoryItemId;
    }
}
