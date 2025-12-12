package com.mugen.backend.entity.inventory;

import com.mugen.backend.enums.ArmorRarity;
import com.mugen.backend.enums.ArmorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade Armor (Armadura)
 * Representa uma armadura no sistema de inventário
 * Focada em proteção e defesa do personagem
 */
@Entity
@Table(name = "armor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Armor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    @NotNull(message = "Nome não pode ser nulo")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ==================== TIPO ====================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "armor_type", nullable = false)
    @NotNull(message = "Nome não pode ser nulo")
    private ArmorType armorType;    // LEVE, NORMAL, PESADA

    @NotNull(message = "Raridade não pode ser nula")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArmorRarity rarity;  // ← NUNCA SERÁ NULL!

    // ==================== PREÇO ====================
    
    @Column(name = "tp_cost", nullable = false)
    private Long tpCost;

    // ==================== BÔNUS DE STATS ====================
    // Armaduras focam em defesa (STR, CON, DEX)
    
    @Column(name = "str_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer strBonus;      // +STR (para armaduras pesadas)

    @Column(name = "con_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer conBonus;      // +CON (defesa principal)

    @Column(name = "dex_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer dexBonus;      // +DEX (para armaduras leves)

    // ==================== REQUISITOS ====================
    
    @Column(name = "min_level", columnDefinition = "INT DEFAULT 1")
    private Integer minLevel;      // Nível mínimo

    @Column(name = "min_con", columnDefinition = "INT DEFAULT 0")
    private Integer minCon;        // CON mínimo para usar

    // ==================== CONTROLE ====================
    
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;      // Disponível para compra?

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Valores padrão
        if (isActive == null) isActive = true;
        if (minLevel == null) minLevel = 1;
        if (minCon == null) minCon = 0;
        
        // Bônus padrão
        if (strBonus == null) strBonus = 0;
        if (conBonus == null) conBonus = 0;
        if (dexBonus == null) dexBonus = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==================== MÉTODOS ÚTEIS ====================
    
    /**
     * Calcula o preço de venda (50% do valor original)
     */
    public Long getSellPrice() {
        return this.tpCost / 2;
    }

    /**
     * Retorna o nome formatado com tipo e raridade
     * Ex: "🛡️ Armadura de Placas Lendária (🌟 Lendário)"
     */
    public String getDisplayName() {
        return String.format("%s %s (%s)", 
            armorType.getIcon(), 
            name, 
            rarity.getDisplayWithIcon()
        );
    }

    /**
     * Retorna descrição completa dos bônus
     */
    public String getBonusDescription() {
        StringBuilder sb = new StringBuilder();
        if (strBonus > 0) sb.append("+").append(strBonus).append(" STR ");
        if (conBonus > 0) sb.append("+").append(conBonus).append(" CON ");
        if (dexBonus > 0) sb.append("+").append(dexBonus).append(" DEX ");
        return sb.length() > 0 ? sb.toString().trim() : "Sem bônus";
    }

    /**
     * Verifica se a armadura está disponível para compra
     */
    public boolean isAvailableForPurchase() {
        return isActive != null && isActive;
    }

    /**
     * Verifica se um personagem atende aos requisitos
     */
    public boolean meetsRequirements(Integer charLevel, Integer charCon) {
        return charLevel >= (minLevel != null ? minLevel : 1) &&
               charCon >= (minCon != null ? minCon : 0);
    }

    /**
     * Retorna descrição dos requisitos
     */
    public String getRequirementsDescription() {
        StringBuilder sb = new StringBuilder();
        if (minLevel != null && minLevel > 1) {
            sb.append("Level ").append(minLevel);
        }
        if (minCon != null && minCon > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("CON ").append(minCon);
        }
        return sb.length() > 0 ? sb.toString() : "Sem requisitos";
    }
}
