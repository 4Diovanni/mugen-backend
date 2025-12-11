package com.mugen.backend.dto.equipment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para breakdown de bônus do equipamento
 * Mostra todos os bônus de atributos e defesa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentBonusesDTO {

    // ==================== ATRIBUTOS ====================
    private int strBonus;
    private int dexBonus;
    private int conBonus;
    private int wilBonus;
    private int mndBonus;
    private int spiBonus;

    // ==================== DEFESA ====================
    private int physicalDefenseBonus;
    private int magicalDefenseBonus;

    // ==================== DANOS ====================
    private int physicalDamageBonus;
    private int magicalDamageBonus;

    // ==================== ESPECIAIS ====================
    private int criticalChanceBonus;

    // ==================== ARMA EQUIPADA ====================
    private String equippedWeaponName;
    private Integer equippedWeaponId;

    // ==================== ARMADURA EQUIPADA ====================
    private String equippedArmorName;
    private Integer equippedArmorId;

    /**
     * Calcula total de bônus de atributos
     */
    public int getTotalAttributeBonus() {
        return strBonus + dexBonus + conBonus + wilBonus + mndBonus + spiBonus;
    }

    /**
     * Calcula total de bônus de defesa
     */
    public int getTotalDefenseBonus() {
        return physicalDefenseBonus + magicalDefenseBonus;
    }

    /**
     * Calcula total de bônus de dano
     */
    public int getTotalDamageBonus() {
        return physicalDamageBonus + magicalDamageBonus;
    }

    /**
     * Verifica se tem equipamento
     */
    public boolean hasEquipment() {
        return equippedWeaponId != null || equippedArmorId != null;
    }

    /**
     * Retorna summary de equipamento
     */
    public String getEquipmentSummary() {
        StringBuilder sb = new StringBuilder();

        if (equippedWeaponId != null) {
            sb.append("Arma: ").append(equippedWeaponName);
        }

        if (equippedArmorId != null) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Armadura: ").append(equippedArmorName);
        }

        if (sb.length() == 0) {
            sb.append("Nenhum equipamento");
        }

        return sb.toString();
    }

    public int getTotalStrBonus() {
        return this.strBonus;
    }

    public int getTotalDexBonus() {
        return this.dexBonus;
    }

    public int getTotalConBonus() {
        return this.conBonus;
    }

    public int getTotalWilBonus() {
        return this.wilBonus;
    }

    public int getTotalMndBonus() {
        return this.mndBonus;
    }

    public int getTotalSpiBonus() {
        return this.spiBonus;
    }

    // ✅ E também o buildSummary():
    public String buildSummary() {
        return String.format(
                "STR: +%d | DEX: +%d | CON: +%d | WIL: +%d | MND: +%d | SPI: +%d",
                strBonus, dexBonus, conBonus, wilBonus, mndBonus, spiBonus
        );
    }
}
