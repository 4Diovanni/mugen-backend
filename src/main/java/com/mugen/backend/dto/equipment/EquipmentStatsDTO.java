package com.mugen.backend.dto.equipment;

import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.dto.inventory.WeaponDTO;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.enums.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ✅ DTO para equipamento com atributos finais do personagem
 * Retorna tanto os bônus do equipamento quanto
 * os atributos finais do personagem (base + equipamento)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStatsDTO {

    // ==================== EQUIPAMENTO ====================
    private UUID characterId;
    private Long equipmentId;
    private boolean hasWeapon;
    private boolean hasArmor;
    private boolean fullyEquipped;
    // Status
    private EquipmentStatus status;             // EQUIPADO, NAO_EQUIPADO
    // ==================== ITENS EQUIPADOS ====================
    // Items equipados
    private WeaponDTO weapon;
    private ArmorDTO armor;
    private String weaponName;
    private Integer weaponId;
    private String armorName;
    private Integer armorId;

    // ==================== BÔNUS DO EQUIPAMENTO ====================
    private int totalStrBonus;
    private int totalDexBonus;
    private int totalConBonus;
    private int totalWilBonus;
    private int totalMndBonus;
    private int totalSpiBonus;

    // ==================== ✅ ATRIBUTOS FINAIS DO PERSONAGEM ====================
    // Importante: Estes são base + bônus do equipamento
    private int finalStr;
    private int finalDex;
    private int finalCon;
    private int finalWil;
    private int finalMnd;
    private int finalSpi;

    // ==================== TIMESTAMPS ====================
    private java.time.LocalDateTime weaponEquippedAt;
    private java.time.LocalDateTime armorEquippedAt;

    // ==================== SUMMARIES ====================
    private String equipmentSummary;
    private String bonusSummary;
    private String attributesSummary;

    /**
     * ✅ NOVO: Retorna apenas os atributos finais
     */
    public CharacterAttribute getFinalAttributes() {
        return CharacterAttribute.builder()
                .str(finalStr)
                .dex(finalDex)
                .con(finalCon)
                .wil(finalWil)
                .mnd(finalMnd)
                .spi(finalSpi)
                .build();
    }

    /**
     * Calcula total de bônus
     */
    public int getTotalBonusSum() {
        return totalStrBonus + totalDexBonus + totalConBonus
                + totalWilBonus + totalMndBonus + totalSpiBonus;
    }

    /**
     * ✅ Summary dos atributos finais
     */
    public String buildAttributesSummary() {
        return String.format(
                "STR: %d | DEX: %d | CON: %d | WIL: %d | MND: %d | SPI: %d",
                finalStr, finalDex, finalCon, finalWil, finalMnd, finalSpi
        );
    }

    /**
     * Summary dos bônus
     */
    public String buildBonusSummary() {
        return String.format(
                "Bônus: +%d STR | +%d DEX | +%d CON | +%d WIL | +%d MND | +%d SPI",
                totalStrBonus, totalDexBonus, totalConBonus,
                totalWilBonus, totalMndBonus, totalSpiBonus
        );
    }
}
