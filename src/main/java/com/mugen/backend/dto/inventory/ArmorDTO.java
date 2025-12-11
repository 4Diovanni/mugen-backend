package com.mugen.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Armor (Armadura)
 * 
 * Usado para transferir dados de armadura entre camadas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArmorDTO {
    
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    
    // Tipo
    private String armorType;          // LEVE, NORMAL, PESADA
    private String armorTypeIcon;      // Ícone do tipo
    private String rarity;             // LENDARIO, EPICO, RARO, etc
    private String rarityIcon;         // Ícone da raridade
    private String rarityColor;        // Cor hex da raridade
    
    // Preço
    private Long tpCost;
    private Long sellPrice;            // 50% do preço
    
    // Bônus de stats (armaduras focam em defesa)
    private Integer strBonus;
    private Integer conBonus;
    private Integer dexBonus;
    
    // Requisitos
    // Requisitos - ADICIONAR @NotNull E @Positive
    @NotNull(message = "Nível mínimo não pode ser nulo")
    @Positive(message = "Nível mínimo deve ser positivo")
    private Integer minLevel;

    @NotNull(message = "Constituição mínima não pode ser nula")
    @Positive(message = "Constituição mínima deve ser positiva")
    private Integer minCon;
    private String requirementsDescription;
    
    // Controle
    private Boolean isActive;
    private Boolean canEquip;          // Se o player pode usar
    
    // Display
    private String displayName;        // Nome formatado com tipo e raridade
    private String bonusDescription;   // Descrição dos bônus
}
