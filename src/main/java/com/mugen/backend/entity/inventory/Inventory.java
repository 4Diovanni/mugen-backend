package com.mugen.backend.entity.inventory;

import com.mugen.backend.entity.character.Character;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Inventory (Inventário)
 * Inventário principal do personagem
 * Gerencia espaço e items (armas, armaduras, materiais)
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTO ====================

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id", nullable = false, unique = true)
    private Character character;

    // ==================== ESPAÇO ====================
    
    @Column(name = "current_slots", columnDefinition = "INT DEFAULT 0")
    private Integer currentSlots;   // Slots usados

    @Column(name = "max_slots", columnDefinition = "INT DEFAULT 50")
    private Integer maxSlots;       // Limite de slots

    // ==================== VALOR TOTAL ====================
    
    @Column(name = "total_value", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalValue;        // Valor total em TP dos items

    // ==================== RELACIONAMENTOS COM ITEMS ====================
    
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryWeapon> weapons = new ArrayList<>();

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryArmor> armors = new ArrayList<>();

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryMaterial> materials = new ArrayList<>();

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
        
        // Valores padrão
        if (currentSlots == null) currentSlots = 0;
        if (maxSlots == null) maxSlots = 50;
        if (totalValue == null) totalValue = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // ==================== MÉTODOS ÚTEIS ====================
    
    /**
     * Verifica se há espaço disponível
     */
    public boolean hasSpace(int slotsNeeded) {
        return (currentSlots + slotsNeeded) <= maxSlots;
    }

    /**
     * Calcula espaço livre
     */
    public int getFreeSlots() {
        return maxSlots - currentSlots;
    }

    /**
     * Calcula porcentagem de uso
     */
    public int getUsagePercentage() {
        if (maxSlots == 0) return 0;
        return (int) ((currentSlots * 100.0) / maxSlots);
    }

    /**
     * Verifica se está cheio
     */
    public boolean isFull() {
        return currentSlots >= maxSlots;
    }

    /**
     * Adiciona slots usados
     */
    public void addSlots(int amount) {
        this.currentSlots = Math.min(currentSlots + amount, maxSlots);
    }

    /**
     * Remove slots usados
     */
    public void removeSlots(int amount) {
        this.currentSlots = Math.max(currentSlots - amount, 0);
    }

    /**
     * Adiciona valor total
     */
    public void addValue(Long amount) {
        this.totalValue += amount;
    }

    /**
     * Remove valor total
     */
    public void subtractValue(Long amount) {
        this.totalValue = Math.max(totalValue - amount, 0L);
    }

    /**
     * Retorna contagem total de items
     */
    public int getTotalItemCount() {
        int weaponCount = weapons.stream().mapToInt(InventoryWeapon::getQuantity).sum();
        int armorCount = armors.stream().mapToInt(InventoryArmor::getQuantity).sum();
        int materialCount = materials.stream().mapToInt(InventoryMaterial::getQuantity).sum();
        return weaponCount + armorCount + materialCount;
    }

    /**
     * Retorna descrição do inventário
     */
    public String getInventoryStatus() {
        return String.format("Inventário: %d/%d slots (%d%% usado) | Valor: %d TP | Items: %d",
            currentSlots, maxSlots, getUsagePercentage(), totalValue, getTotalItemCount()
        );
    }
}
