package com.mugen.backend.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * Entidade InventoryWeapon
 * 
 * Representa uma arma no inventário do personagem
 * Tabela intermediária entre Inventory e Weapon
 */
@Entity
@Table(name = "inventory_weapon", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_id", "weapon_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryWeapon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weapon_id", nullable = false)
    private Weapon weapon;

    // ==================== QUANTIDADE ====================
    
    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer quantity;       // Quantidade desta arma

    // ==================== INFO DE COMPRA ====================
    
    @Column(name = "purchased_at_level")
    private Integer purchasedAtLevel;   // Level quando comprou

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @PrePersist
    protected void onCreate() {
        acquiredAt = LocalDateTime.now();
        if (quantity == null) quantity = 1;
    }

    // ==================== MÉTODOS ÚTEIS ====================
    
    /**
     * Adiciona quantidade
     */
    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    /**
     * Remove quantidade
     */
    public void removeQuantity(int amount) {
        this.quantity = Math.max(this.quantity - amount, 0);
    }

    /**
     * Verifica se tem quantidade
     */
    public boolean hasQuantity(int amount) {
        return this.quantity >= amount;
    }

    /**
     * Calcula valor total (quantidade * preço)
     */
    public Long getTotalValue() {
        if (weapon == null) return 0L;
        return weapon.getTpCost() * quantity;
    }

    /**
     * Calcula valor de venda total (50% do preço)
     */
    public Long getTotalSellValue() {
        if (weapon == null) return 0L;
        return weapon.getSellPrice() * quantity;
    }
}
