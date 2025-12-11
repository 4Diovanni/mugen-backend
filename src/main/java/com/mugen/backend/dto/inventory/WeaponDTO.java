package com.mugen.backend.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Weapon (Arma)
 * 
 * Usado para transferir dados de arma entre camadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeaponDTO {
    
    private Integer id;
    private String name;
    private String description;
    private String notes;
    private String imageUrl;
    
    // Tipos
    @NotBlank(message = "Skill type is required")
    @Pattern(
            regexp = "ATAQUE|DEFESA|UTILIDADE|SUPORTE",
            message = "Invalid skill type. Must be: ATAQUE, DEFESA, UTILIDADE, or SUPORTE"
    )  // ✅ CORRIGIDO
    private String primaryType;        // ATAQUE, DEFESA, UTILIDADE, SUPORTE
    private String secondaryType;      // ESPADA, ARCO, MAGIA, etc
    private String elementalType;      // FIRE, ICE, WIND, WATER, DARKNESS, LIGHT, EARTH
    private String rarity;             // LENDARIO, EPICO, RARO, etc
    private String rarityIcon;         // Ícone da raridade
    private String rarityColor;        // Cor hex da raridade
    
    // Preço
    private Long tpCost;
    private Long sellPrice;            // 50% do preço
    
    // Bônus de stats
    private Integer strBonus;
    private Integer dexBonus;
    private Integer conBonus;
    private Integer wilBonus;
    private Integer mndBonus;
    private Integer spiBonus;
    
    // Requisitos
    private WeaponRequirementsDTO requirements;
    
    // Controle
    private Boolean isUnique;
    private Integer maxQuantity;
    private Boolean isActive;
    private Boolean canEquip;          // Se o player pode usar (baseado em requisitos)
    
    // Display
    private String displayName;        // Nome formatado com raridade
    private String bonusDescription;   // Descrição dos bônus
}
