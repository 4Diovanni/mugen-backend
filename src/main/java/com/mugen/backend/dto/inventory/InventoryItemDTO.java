package com.mugen.backend.dto.inventory;

import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.dto.inventory.MaterialDTO;
import com.mugen.backend.dto.inventory.WeaponDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para Item no Inventário
 * 
 * Representa um item (arma, armadura ou material) no inventário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDTO {
    
    private Long id;
    private String itemType;           // WEAPON, ARMOR, MATERIAL
    
    // Item detalhado (apenas um será preenchido)
    private WeaponDTO weapon;
    private ArmorDTO armor;
    private MaterialDTO material;
    
    // Quantidade
    private Integer quantity;
    
    // Info de compra
    private Integer purchasedAtLevel;  // Level quando foi adquirido
    private LocalDateTime acquiredAt;
    
    // Valores
    private Long unitValue;            // Valor unitário em TP
    private Long totalValue;           // Valor total (unitValue * quantity)
    private Long sellValue;            // Valor de venda total (50%)
    
    // Status
    private Boolean isEquipped;        // Se está equipado
    
    // Display
    private String displayInfo;        // Ex: "3x 🌟 Excalibur (Lendário)"
}
