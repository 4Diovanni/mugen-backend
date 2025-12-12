package com.mugen.backend.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade InventoryArmor
 * Representa uma armadura no inventário do personagem
 * Tabela intermediária entre Inventory e Armor
 */
@Entity
@Table(name = "inventory_armor", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_id", "armor_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryArmor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armor_id", nullable = false)
    private Armor armor;

    // ==================== QUANTIDADE ====================
    
    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer quantity;

    // ==================== INFO DE COMPRA ====================
    
    @Column(name = "purchased_at_level")
    private Integer purchasedAtLevel;

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt;

    @PrePersist
    protected void onCreate() {
        acquiredAt = LocalDateTime.now();
        if (quantity == null) quantity = 1;
    }

    // ==================== MÉTODOS ÚTEIS ====================
    
    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        this.quantity = Math.max(this.quantity - amount, 0);
    }

    public boolean hasQuantity(int amount) {
        return this.quantity >= amount;
    }

    public Long getTotalValue() {
        if (armor == null) return 0L;
        return armor.getTpCost() * quantity;
    }

    public Long getTotalSellValue() {
        if (armor == null) return 0L;
        return armor.getSellPrice() * quantity;
    }
}
