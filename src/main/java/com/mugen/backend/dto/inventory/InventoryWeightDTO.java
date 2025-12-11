package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para informações de peso/slots do inventário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryWeightDTO {

    private long totalWeapons;
    private long totalArmors;
    private long totalMaterials;
    private long totalItems;

    /**
     * Calcula total de items
     */
    public void calculateTotal() {
        this.totalItems = totalWeapons + totalArmors + totalMaterials;
    }

    /**
     * Retorna porcentagem de uso (0-100)
     */
    public double getUsagePercentage() {
        // Assumindo limite de 100 items total
        return (totalItems / 100.0) * 100;
    }

    /**
     * Verifica se inventário está cheio
     */
    public boolean isFull() {
        return totalItems >= 100;
    }
}
