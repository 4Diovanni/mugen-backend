package com.mugen.backend.service;

import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.entity.character.CharacterEquipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Calcula atributos finais com bônus de equipamento
 * Responsável por combinar:
 * - Atributos-base do personagem
 * - Bônus do equipamento
 * = Atributos finais
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterAttributesCalculatorService {

    /**
     * Calcula atributos FINAIS do personagem (base + equipamento)
     * 
     * @param baseAttributes Atributos-base do personagem (sem equipamento)
     * @param equipment Equipamento atual do personagem
     * @return Atributos finais com bônus aplicados
     */
    public CharacterAttribute calculateFinalAttributes(
            CharacterAttribute baseAttributes,
            CharacterEquipment equipment) {
        
        log.debug("Calculando atributos finais com bônus de equipamento");
        
        // Cria uma cópia dos atributos-base
        CharacterAttribute finalAttributes = CharacterAttribute.builder()
                .characterId(baseAttributes.getCharacterId())
                .str(baseAttributes.getStr())
                .dex(baseAttributes.getDex())
                .con(baseAttributes.getCon())
                .wil(baseAttributes.getWil())
                .mnd(baseAttributes.getMnd())
                .spi(baseAttributes.getSpi())
                .build();

        // Adiciona bônus do equipamento
        finalAttributes.setStr(finalAttributes.getStr() + equipment.getTotalStrBonus());
        finalAttributes.setDex(finalAttributes.getDex() + equipment.getTotalDexBonus());
        finalAttributes.setCon(finalAttributes.getCon() + equipment.getTotalConBonus());
        finalAttributes.setWil(finalAttributes.getWil() + equipment.getTotalWilBonus());
        finalAttributes.setMnd(finalAttributes.getMnd() + equipment.getTotalMndBonus());
        finalAttributes.setSpi(finalAttributes.getSpi() + equipment.getTotalSpiBonus());

        log.debug("✅ Atributos finais calculados: STR={}, DEX={}, CON={}, WIL={}, MND={}, SPI={}",
                finalAttributes.getStr(),
                finalAttributes.getDex(),
                finalAttributes.getCon(),
                finalAttributes.getWil(),
                finalAttributes.getMnd(),
                finalAttributes.getSpi()
        );

        return finalAttributes;
    }

    /**
     * Apenas retorna os bônus do equipamento
     */
    public int calculateTotalBonus(CharacterEquipment equipment) {
        return equipment.getTotalStrBonus()
                + equipment.getTotalDexBonus()
                + equipment.getTotalConBonus()
                + equipment.getTotalWilBonus()
                + equipment.getTotalMndBonus()
                + equipment.getTotalSpiBonus();
    }
}
