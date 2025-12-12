package com.mugen.backend.service;

import com.mugen.backend.dto.character.CharacterStats;
import com.mugen.backend.entity.Race;
import com.mugen.backend.entity.Transformation;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterStatService {

    /**
     * Calcula todos os stats finais do personagem baseado em:
     * - Atributos-base (STR, DEX, CON, WIL, MND, SPI)
     * - Modificador da raça
     * - Multiplicador da transformação ativa
     */
    public CharacterStats calculateStats(Character character) {
        log.debug("Calculating stats for character: {}", character.getName());

        CharacterAttribute attr = character.getAttributes();
        Race race = character.getRace();
        Transformation transformation = character.getActiveTransformation();

        // Modificadores
        double raceModifier = race.getRaceClassModifier().doubleValue();
        double transformationMultiplier = transformation != null
                ? transformation.getMultiplier().doubleValue()
                : 1.0;

        // Calcular stats
        CharacterStats stats = CharacterStats.builder()
                // Stats de Combate
                .meleeDamage(calculateMeleeDamage(attr.getStr(), raceModifier, transformationMultiplier))
                .kiPower(calculateKiPower(attr.getWil(), raceModifier, transformationMultiplier))
                .speed(calculateSpeed(attr.getDex(), transformationMultiplier))

                // Stats de Defesa
                .physicalDefense(calculatePhysicalDefense(attr.getCon(), raceModifier))
                .kiDefense(calculateKiDefense(attr.getSpi(), raceModifier))
                .mentalDefense(calculateMentalDefense(attr.getMnd(), raceModifier))

                // Stats de Recursos
                .maxHp(calculateMaxHp(attr.getCon()))
                .maxKi(calculateMaxKi(attr.getSpi()))
                .actionTime(calculateActionTime(attr.getCon()))

                // Modificadores
                .raceModifier(race.getRaceClassModifier())
                .transformationMultiplier(transformation != null
                        ? transformation.getMultiplier()
                        : BigDecimal.ONE)

                // Stats Base
                .baseStr(attr.getStr())
                .baseDex(attr.getDex())
                .baseCon(attr.getCon())
                .baseWil(attr.getWil())
                .baseMnd(attr.getMnd())
                .baseSpi(attr.getSpi())
                .build();

        log.debug("Calculated stats - MeleeDmg: {}, KiPower: {}, Speed: {}",
                stats.getMeleeDamage(), stats.getKiPower(), stats.getSpeed());

        return stats;
    }

    /**
     * Calcula dano físico
     * Fórmula: STR * 2.5 * modificador_raça * multiplicador_transformação
     */
    private Double calculateMeleeDamage(Integer str, double raceModifier, double transformationMultiplier) {
        return str * 2.5 * raceModifier * transformationMultiplier;
    }

    /**
     * Calcula poder de Ki
     * Fórmula: WIL * 5.2 * modificador_raça * multiplicador_transformação
     */
    private Double calculateKiPower(Integer wil, double raceModifier, double transformationMultiplier) {
        return wil * 5.2 * raceModifier * transformationMultiplier;
    }

    /**
     * Calcula velocidade
     * Fórmula: 100 + (DEX * 1) * multiplicador_transformação
     */
    private Double calculateSpeed(Integer dex, double transformationMultiplier) {
        return 100 + (dex * 1.0) * transformationMultiplier;
    }

    /**
     * Calcula defesa física
     * Fórmula: CON * 1.8 * modificador_raça
     */
    private Double calculatePhysicalDefense(Integer con, double raceModifier) {
        return con * 1.8 * raceModifier;
    }

    /**
     * Calcula defesa contra Ki
     * Fórmula: SPI * 2.0 * modificador_raça
     */
    private Double calculateKiDefense(Integer spi, double raceModifier) {
        return spi * 2.0 * raceModifier;
    }

    /**
     * Calcula defesa mental
     * Fórmula: MND * 1.5 * modificador_raça
     */
    private Double calculateMentalDefense(Integer mnd, double raceModifier) {
        return mnd * 1.5 * raceModifier;
    }

    /**
     * Calcula HP máximo
     * Fórmula: CON * 20
     */
    private Integer calculateMaxHp(Integer con) {
        return con * 20;
    }

    /**
     * Calcula Ki máximo
     * Fórmula: SPI * 40
     */
    private Integer calculateMaxKi(Integer spi) {
        return spi * 40;
    }

    /**
     * Calcula tempo de ação (para sistema de turnos)
     * Fórmula: CON * 3.5
     */
    private Double calculateActionTime(Integer con) {
        return con * 3.5;
    }

    /**
     * Calcula ‘boost’ de stats ao ativar transformação
     */
    public CharacterStats calculateTransformationBoost(Character character, Transformation newTransformation) {
        log.debug("Calculating transformation boost for: {}", newTransformation.getName());

        // Stats atuais (sem transformação)
        Transformation currentTransformation = character.getActiveTransformation();
        character.setActiveTransformation(null);
        CharacterStats baseStats = calculateStats(character);

        // Stats com nova transformação
        character.setActiveTransformation(newTransformation);
        CharacterStats boostedStats = calculateStats(character);

        // Restaurar transformação original
        character.setActiveTransformation(currentTransformation);

        // Retornar diferença
        return CharacterStats.builder()
                .meleeDamage(boostedStats.getMeleeDamage() - baseStats.getMeleeDamage())
                .kiPower(boostedStats.getKiPower() - baseStats.getKiPower())
                .speed(boostedStats.getSpeed() - baseStats.getSpeed())
                .physicalDefense(boostedStats.getPhysicalDefense() - baseStats.getPhysicalDefense())
                .kiDefense(boostedStats.getKiDefense() - baseStats.getKiDefense())
                .mentalDefense(boostedStats.getMentalDefense() - baseStats.getMentalDefense())
                .transformationMultiplier(newTransformation.getMultiplier())
                .build();
    }
}
