package com.mugen.backend.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade InventoryMaterial
 * Representa um material no inventário do personagem
 * Tabela intermediária entre Inventory e Material
 */
@Entity
@Table(name = "inventory_material", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"inventory_id", "material_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // ==================== QUANTIDADE ====================
    
    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer quantity;

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

    /**
     * Retorna descrição do material com quantidade
     * Ex: "5x 💎 Escama de Dragão"
     */
    public String getDisplayInfo() {
        if (material == null) return "Material desconhecido";
        return String.format("%dx %s", quantity, material.getDisplayName());
    }
    // ==================== INFO DE COMPRA ====================

    @Column(name = "purchased_at_level")
    private Integer purchasedAtLevel;
}
