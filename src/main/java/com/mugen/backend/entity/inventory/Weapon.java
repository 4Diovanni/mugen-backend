package com.mugen.backend.entity.inventory;


import com.mugen.backend.enums.ElementalType;
import com.mugen.backend.enums.WeaponPrimaryType;
import com.mugen.backend.enums.WeaponRarity;
import com.mugen.backend.enums.WeaponSecondaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade Weapon (Arma)
 * Representa uma arma no sistema de inventário
 * Contém todos os atributos necessários para compra, venda e equipamento
 */
@Entity
@Table(name = "weapon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    @NotNull(message = "Nome não pode ser nulo")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ==================== TIPOS ====================
    
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_type")
    private WeaponPrimaryType primaryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_type")
    @NotNull(message = "Nome não pode ser nulo")
    private WeaponSecondaryType secondaryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "elemental_type")
    private ElementalType elementalType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Nome não pode ser nulo")
    private WeaponRarity rarity;

    // ==================== REQUISITOS ====================
    
    @Embedded
    private WeaponRequirements requirements;

    // ==================== PREÇO ====================
    
    @Column(name = "tp_cost", nullable = false)
    private Long tpCost;

    // ==================== BÔNUS DE STATS ====================
    // Aplicados diretamente aos stats do Character
    
    @Column(name = "str_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer strBonus;      // +STR (Força)

    @Column(name = "dex_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer dexBonus;      // +DEX (Destreza)

    @Column(name = "con_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer conBonus;      // +CON (Constituição)

    @Column(name = "wil_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer wilBonus;      // +WIL (Vontade)

    @Column(name = "mnd_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer mndBonus;      // +MND (Mente)

    @Column(name = "spi_bonus", columnDefinition = "INT DEFAULT 0")
    private Integer spiBonus;      // +SPI (Espírito)

    // ==================== CONTROLE ====================
    
    @Column(name = "is_unique", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUnique;      // Arma única? (só pode ter 1)

    @Column(name = "max_quantity", columnDefinition = "INT DEFAULT 99")
    private Integer maxQuantity;   // Quantidade máxima permitida

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
        if (isUnique == null) isUnique = false;
        if (maxQuantity == null) maxQuantity = 99;
        if (isActive == null) isActive = true;
        
        // Bônus padrão
        if (strBonus == null) strBonus = 0;
        if (dexBonus == null) dexBonus = 0;
        if (conBonus == null) conBonus = 0;
        if (wilBonus == null) wilBonus = 0;
        if (mndBonus == null) mndBonus = 0;
        if (spiBonus == null) spiBonus = 0;
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
     * Retorna o nome formatado com raridade
     * Ex: "🌟 Excalibur (Lendário)"
     */
    public String getDisplayName() {
        return String.format("%s %s (%s)", 
            rarity.getIcon(), 
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
        if (dexBonus > 0) sb.append("+").append(dexBonus).append(" DEX ");
        if (conBonus > 0) sb.append("+").append(conBonus).append(" CON ");
        if (wilBonus > 0) sb.append("+").append(wilBonus).append(" WIL ");
        if (mndBonus > 0) sb.append("+").append(mndBonus).append(" MND ");
        if (spiBonus > 0) sb.append("+").append(spiBonus).append(" SPI ");
        return sb.length() > 0 ? sb.toString().trim() : "Sem bônus";
    }

    /**
     * Verifica se a arma está disponível para compra
     */
    public boolean isAvailableForPurchase() {
        return isActive != null && isActive;
    }
}
