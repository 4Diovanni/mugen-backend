package com.mugen.backend.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Material
 * Usado para transferir dados de material entre camadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialDTO {
    
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    
    // Raridade
    private String rarity;             // LENDARIO, EPICO, RARO, etc
    private String rarityIcon;         // Ícone da raridade
    private String rarityColor;        // Cor hex da raridade
    
    // Controle
    private Boolean addedByMaster;     // Sempre TRUE
    private LocalDateTime createdAt;
    private Integer purchasedAtLevel;
    
    // Display
    private String displayName;        // Nome formatado com raridade

}
