package com.mugen.backend.entity.inventory;

import com.mugen.backend.enums.WeaponRarity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade Material
 * Representa materiais usados para crafting/forja
 * Gerenciados pelo MASTER do RPG
 */
@Entity
@Table(name = "material")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ==================== RARIDADE ====================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeaponRarity rarity;    // Usa mesma raridade

    // ==================== CONTROLE ====================
    
    @Column(name = "added_by_master", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean addedByMaster;  // Sempre TRUE (só MASTER adiciona)

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        
        // MASTER sempre adiciona
        if (addedByMaster == null) {
            addedByMaster = true;
        }
    }

    // ==================== MÉTODOS ÚTEIS ====================
    
    /**
     * Retorna o nome formatado com raridade
     * Ex: "💎 Escama de Dragão (Raro)"
     */
    public String getDisplayName() {
        return String.format("%s %s (%s)", 
            rarity.getIcon(), 
            name, 
            rarity.getDisplayName()
        );
    }

    /**
     * Verifica se foi adicionado pelo MASTER
     */
    public boolean isAddedByMaster() {
        return addedByMaster != null && addedByMaster;
    }
}
