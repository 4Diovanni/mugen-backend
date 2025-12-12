package com.mugen.backend.dto.equipment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para equipar item (ARMA ou ARMADURA)
 * Usado pelos endpoints de equipar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipItemRequest {

    @NotNull(message = "ID do item no inventário é obrigatório")
    @Positive(message = "ID deve ser positivo")
    private Long inventoryItemId;

    @NotNull(message = "Tipo do item é obrigatório")
    private String itemType; // WEAPON, ARMOR

    /**
     * ✅ MÉTODO CORRIGIDO - Era getItemId(), agora getInventoryItemId()
     * Compatível com SellItemRequest
     */
    public Long getItemId() {
        return this.inventoryItemId;
    }

    /**
     * Setter compatível
     */
    public void setItemId(Long itemId) {
        this.inventoryItemId = itemId;
    }


}