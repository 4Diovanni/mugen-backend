package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response de venda de item
 * 
 * Retornado após vender um item com sucesso
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellItemResponse {
    
    private Boolean success;
    private String message;
    
    // Item vendido
    private String itemName;
    private String itemType;           // WEAPON, ARMOR, MATERIAL
    private Integer quantitySold;
    
    // Financeiro
    private Long tpReceived;           // TP recebido (50% do valor)
    private Long tpBalance;            // Saldo atual de TP
    
    // Inventário
    private Integer currentSlots;
    private Integer maxSlots;
    private Integer slotsFreed;        // Slots liberados
    
    // Display
    private String sellSummary;        // Ex: "Vendeu 2x Excalibur por 400 TP"
}
