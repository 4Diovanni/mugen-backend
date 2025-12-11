package com.mugen.backend;

import com.mugen.backend.dto.character.CharacterStats;
import com.mugen.backend.entity.*;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.service.CharacterStatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CharacterStatServiceTest {

    private CharacterStatService statService;

    @BeforeEach
    void setUp() {
        statService = new CharacterStatService();
    }

    @Test
    void testCalculateStats_WithoutTransformation() {
        // Arrange
        Race race = Race.builder()
                .name("Saiyan")
                .raceClassModifier(new BigDecimal("1.2"))
                .build();

        CharacterAttribute attributes = CharacterAttribute.builder()
                .str(50)
                .dex(40)
                .con(45)
                .wil(55)
                .mnd(35)
                .spi(60)
                .build();

        Character character = Character.builder()
                .name("Goku")
                .race(race)
                .attributes(attributes)
                .activeTransformation(null)
                .build();

        // Act
        CharacterStats stats = statService.calculateStats(character);

        // Assert
        assertNotNull(stats);
        assertEquals(150.0, stats.getMeleeDamage(), 0.01); // 50 * 2.5 * 1.2 = 150
        assertEquals(343.2, stats.getKiPower(), 0.01);     // 55 * 5.2 * 1.2 = 343.2
        assertEquals(140.0, stats.getSpeed(), 0.01);       // 100 + (40 * 1) = 140
        assertEquals(900, stats.getMaxHp());               // 45 * 20 = 900
        assertEquals(2400, stats.getMaxKi());              // 60 * 40 = 2400
        assertEquals(157.5, stats.getActionTime(), 0.01);  // 45 * 3.5 = 157.5
    }

    @Test
    void testCalculateStats_WithTransformation() {
        // Arrange
        Race race = Race.builder()
                .name("Saiyan")
                .raceClassModifier(new BigDecimal("1.2"))
                .build();

        Transformation transformation = Transformation.builder()
                .name("Super Saiyan")
                .multiplier(new BigDecimal("50"))
                .build();

        CharacterAttribute attributes = CharacterAttribute.builder()
                .str(50)
                .dex(40)
                .con(45)
                .wil(55)
                .mnd(35)
                .spi(60)
                .build();

        Character character = Character.builder()
                .name("Goku")
                .race(race)
                .attributes(attributes)
                .activeTransformation(transformation)
                .build();

        // Act
        CharacterStats stats = statService.calculateStats(character);

        // Assert
        assertNotNull(stats);
        assertEquals(7500.0, stats.getMeleeDamage(), 0.01); // 50 * 2.5 * 1.2 * 50 = 7500
        assertEquals(17160.0, stats.getKiPower(), 0.01);    // 55 * 5.2 * 1.2 * 50 = 17160
        assertEquals(2100.0, stats.getSpeed(), 0.01);       // 100 + (40 * 1 * 50) = 2100
    }
}
