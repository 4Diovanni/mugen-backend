package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response de compra de item (arma/armadura)
 * Retornado após comprar um item com sucesso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyItemResponse {
    
    private Boolean success;
    private String message;
    
    // Item comprado
    private Long inventoryItemId;
    private Integer itemId;
    private String itemName;
    private String itemType;           // WEAPON, ARMOR
    private Integer quantity;
    
    // Financeiro
    private Long tpSpent;              // TP gasto na compra
    private Long tpBalance;            // Saldo atual de TP
    
    // Inventário
    private Integer currentSlots;
    private Integer maxSlots;
    private Integer slotsUsed;         // Slots usados nesta compra
    
    // Display
    private String purchaseSummary;    // Ex: "Comprou 2x Excalibur por 800 TP"
}
