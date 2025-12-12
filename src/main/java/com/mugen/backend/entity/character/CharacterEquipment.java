package com.mugen.backend.entity.character;

import com.mugen.backend.entity.inventory.Armor;
import com.mugen.backend.entity.inventory.Weapon;
import com.mugen.backend.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade CharacterEquipment
 * Representa o equipamento ativo do personagem
 * Simplificado para viés narrativo: apenas EQUIPADO ou NAO_EQUIPADO
 */
@Entity
@Table(name = "character_equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterEquipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTO ====================
    
    @Column(name = "character_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID characterId;

    // ==================== ITEMS EQUIPADOS ====================
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weapon_id")
    private Weapon weapon;          // Arma equipada (pode ser null)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armor_id")
    private Armor armor;            // Armadura equipada (pode ser null)

    // ==================== STATUS ====================
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    // ==================== TIMESTAMPS ====================
    
    @Column(name = "equipped_at")
    private LocalDateTime equippedAt;

    @Column(name = "unequipped_at")
    private LocalDateTime unequippedAt;

    // ==================== MÉTODOS ÚTEIS ====================


    /**
     * Verifica se está equipado
     */
    public boolean isEquipped() {
        return this.status == EquipmentStatus.EQUIPADO_AA;
    }

    /**
     * Verifica se tem arma equipada
     */
    public boolean hasWeapon() {
        return weapon != null;
    }

    /**
     * Verifica se tem armadura equipada
     */
    public boolean hasArmor() {
        return armor != null;
    }

    /**
     * Calcula bônus total de STR
     */
    public int getTotalStrBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getStrBonus();
        if (hasArmor()) bonus += armor.getStrBonus();
        return bonus;
    }

    /**
     * Calcula bônus total de DEX
     */
    public int getTotalDexBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getDexBonus();
        if (hasArmor()) bonus += armor.getDexBonus();
        return bonus;
    }

    /**
     * Calcula bônus total de CON
     */
    public int getTotalConBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getConBonus();
        if (hasArmor()) bonus += armor.getConBonus();
        return bonus;
    }

    /**
     * Calcula bônus total de WIL
     */
    public int getTotalWilBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getWilBonus();
        return bonus;
    }

    /**
     * Calcula bônus total de MND
     */
    public int getTotalMndBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getMndBonus();
        return bonus;
    }

    /**
     * Calcula bônus total de SPI
     */
    public int getTotalSpiBonus() {
        int bonus = 0;
        if (hasWeapon()) bonus += weapon.getSpiBonus();
        return bonus;
    }

    /**
     * Retorna descrição completa do equipamento
     */
    public String getEquipmentSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Equipamento [").append(status.getDisplayName()).append("]: ");
        
        if (hasWeapon()) {
            sb.append("\n  Arma: ").append(weapon.getDisplayName());
        } else {
            sb.append("\n  Arma: Nenhuma");
        }
        
        if (hasArmor()) {
            sb.append("\n  Armadura: ").append(armor.getDisplayName());
        } else {
            sb.append("\n  Armadura: Nenhuma");
        }
        
        return sb.toString();
    }

    /**
     * Retorna descrição dos bônus totais
     */
    public String getTotalBonusDescription() {
        return String.format(
            "Bônus Total: +%d STR, +%d DEX, +%d CON, +%d WIL, +%d MND, +%d SPI",
            getTotalStrBonus(), getTotalDexBonus(), getTotalConBonus(),
            getTotalWilBonus(), getTotalMndBonus(), getTotalSpiBonus()
        );
    }

}
