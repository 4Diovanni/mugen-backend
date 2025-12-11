package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para request de compra de material
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyMaterialRequest {

    private Integer materialId;
    private Integer quantity;

    /**
     * Validação básica
     */
    public boolean isValid() {
        return materialId != null && materialId > 0 && 
               quantity != null && quantity > 0 && quantity <= 999;
    }
}
