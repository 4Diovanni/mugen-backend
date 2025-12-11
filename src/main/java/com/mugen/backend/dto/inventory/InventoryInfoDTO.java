package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO para Inventory (Inventário completo)
 * 
 * Retorna informações completas do inventário do personagem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryInfoDTO {
    
    private Long inventoryId;
    private UUID characterId;
    
    // Espaço
    private Integer currentSlots;
    private Integer maxSlots;
    private Integer freeSlots;
    private Integer usagePercentage;
    private Boolean isFull;
    
    // Valor
    private Long totalValue;           // Valor total em TP
    
    // Items
    @Builder.Default
    private List<InventoryItemDTO> weapons = new ArrayList<>();
    
    @Builder.Default
    private List<InventoryItemDTO> armors = new ArrayList<>();
    
    @Builder.Default
    private List<InventoryItemDTO> materials = new ArrayList<>();
    
    // Contadores
    private Integer totalWeapons;
    private Integer totalArmors;
    private Integer totalMaterials;
    private Integer totalItems;
    
    // Timestamps
    private LocalDateTime lastUpdated;
    
    // Display
    private String statusMessage;      // "Inventário: 30/50 slots (60% usado)"
}
