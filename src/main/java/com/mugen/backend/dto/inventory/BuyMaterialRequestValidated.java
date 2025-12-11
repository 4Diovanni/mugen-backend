package com.mugen.backend.dto.inventory;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para request de compra de material - COM VALIDAÇÕES
 * Use esta versão ao invés da anterior!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyMaterialRequestValidated {

    @NotNull(message = "materialId cannot be null")
    @Positive(message = "materialId must be a positive number")
    private Integer materialId;

    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity must be greater than 0")
    @Max(value = 999, message = "quantity cannot exceed 999")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    /**
     * Validação customizada
     */
    public boolean isValid() {
        return materialId != null && materialId > 0 && 
               quantity != null && quantity > 0 && quantity <= 999;
    }

    /**
     * Retorna summary
     */
    @Override
    public String toString() {
        return "BuyMaterialRequest{" +
                "materialId=" + materialId +
                ", quantity=" + quantity +
                '}';
    }
}
