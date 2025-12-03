package com.mugen.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterStats {

    // Stats de Combate
    private Double meleeDamage;      // Dano físico (baseado em STR)
    private Double kiPower;          // Poder de Ki (baseado em WIL)
    private Double speed;            // Velocidade (baseado em DEX)

    // Stats de Defesa
    private Double physicalDefense;  // Defesa física (baseado em CON)
    private Double kiDefense;        // Defesa contra Ki (baseado em SPI)
    private Double mentalDefense;    // Defesa mental (baseado em MND)

    // Stats de Recursos
    private Integer maxHp;           // HP máximo (baseado em CON)
    private Integer maxKi;           // Ki máximo (baseado em SPI)
    private Double actionTime;       // Tempo de ação (baseado em CON)

    // Modificadores
    private BigDecimal raceModifier;         // Modificador da raça
    private BigDecimal transformationMultiplier; // Multiplicador da transformação

    // Stats Base (para referência)
    private Integer baseStr;
    private Integer baseDex;
    private Integer baseCon;
    private Integer baseWil;
    private Integer baseMnd;
    private Integer baseSpi;
}
