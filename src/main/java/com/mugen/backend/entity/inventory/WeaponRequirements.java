package com.mugen.backend.entity.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requisitos para usar uma arma
 * Classe Embeddable que define os requisitos mínimos de stats
 * para equipar uma arma
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeaponRequirements {
    
    @Column(name = "min_str", columnDefinition = "INT DEFAULT 0")
    private Integer minStr;      // Mínimo STR (Força)

    @Column(name = "min_dex", columnDefinition = "INT DEFAULT 0")
    private Integer minDex;      // Mínimo DEX (Destreza)

    @Column(name = "min_con", columnDefinition = "INT DEFAULT 0")
    private Integer minCon;      // Mínimo CON (Constituição)

    @Column(name = "min_wil", columnDefinition = "INT DEFAULT 0")
    private Integer minWil;      // Mínimo WIL (Vontade)

    @Column(name = "min_mnd", columnDefinition = "INT DEFAULT 0")
    private Integer minMnd;      // Mínimo MND (Mente)

    @Column(name = "min_spi", columnDefinition = "INT DEFAULT 0")
    private Integer minSpi;      // Mínimo SPI (Espírito)

    @Column(name = "min_level", columnDefinition = "INT DEFAULT 1")
    private Integer minLevel;    // Nível mínimo

    /**
     * Verifica se um personagem atende aos requisitos
     * 
     * @param charStr STR do personagem
     * @param charDex DEX do personagem
     * @param charCon CON do personagem
     * @param charWil WIL do personagem
     * @param charMnd MND do personagem
     * @param charSpi SPI do personagem
     * @param charLevel Level do personagem
     * @return true se atende todos os requisitos
     */
    public boolean meetsRequirements(
            Integer charStr, Integer charDex, Integer charCon,
            Integer charWil, Integer charMnd, Integer charSpi,
            Integer charLevel) {
        
        return charStr >= (minStr != null ? minStr : 0) &&
               charDex >= (minDex != null ? minDex : 0) &&
               charCon >= (minCon != null ? minCon : 0) &&
               charWil >= (minWil != null ? minWil : 0) &&
               charMnd >= (minMnd != null ? minMnd : 0) &&
               charSpi >= (minSpi != null ? minSpi : 0) &&
               charLevel >= (minLevel != null ? minLevel : 1);
    }

    /**
     * Retorna uma descrição legível dos requisitos
     */
    public String getRequirementsDescription() {
        StringBuilder sb = new StringBuilder();
        if (minLevel != null && minLevel > 1) {
            sb.append("Level ").append(minLevel);
        }
        if (minStr != null && minStr > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("STR ").append(minStr);
        }
        if (minDex != null && minDex > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("DEX ").append(minDex);
        }
        if (minCon != null && minCon > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("CON ").append(minCon);
        }
        if (minWil != null && minWil > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("WIL ").append(minWil);
        }
        if (minMnd != null && minMnd > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("MND ").append(minMnd);
        }
        if (minSpi != null && minSpi > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("SPI ").append(minSpi);
        }
        return sb.length() > 0 ? sb.toString() : "Sem requisitos";
    }
}
