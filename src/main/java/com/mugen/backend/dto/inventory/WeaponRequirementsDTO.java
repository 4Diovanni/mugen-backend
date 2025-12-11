package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para WeaponRequirements
 * 
 * Requisitos necessários para equipar uma arma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeaponRequirementsDTO {
    
    private Integer minStr;      // Mínimo STR
    private Integer minDex;      // Mínimo DEX
    private Integer minCon;      // Mínimo CON
    private Integer minWil;      // Mínimo WIL
    private Integer minMnd;      // Mínimo MND
    private Integer minSpi;      // Mínimo SPI
    private Integer minLevel;    // Nível mínimo
    
    private String description;  // Descrição formatada dos requisitos
}
