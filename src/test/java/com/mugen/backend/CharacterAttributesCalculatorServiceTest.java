package com.mugen.backend;

import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.entity.character.CharacterEquipment;
import com.mugen.backend.service.CharacterAttributesCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CharacterAttributesCalculatorServiceTest {

    @InjectMocks
    private CharacterAttributesCalculatorService calculatorService;

    private CharacterAttribute baseAttributes;
    private CharacterEquipment equipment;

    @BeforeEach
    void setUp() {
        // Atributos base
        baseAttributes = CharacterAttribute.builder()
                .characterId(UUID.randomUUID())
                .str(10)
                .dex(12)
                .con(15)
                .wil(8)
                .mnd(11)
                .spi(14)
                .build();

        // Mock do equipamento para simular bônus
        equipment = mock(CharacterEquipment.class);
    }

    @Test
    void calculateFinalAttributes_shouldAddEquipmentBonusToBaseAttributes() {
        // Configurar bônus do equipamento
        when(equipment.getTotalStrBonus()).thenReturn(5);
        when(equipment.getTotalDexBonus()).thenReturn(0);
        when(equipment.getTotalConBonus()).thenReturn(2);
        when(equipment.getTotalWilBonus()).thenReturn(-1); // Testando bônus negativo
        when(equipment.getTotalMndBonus()).thenReturn(3);
        when(equipment.getTotalSpiBonus()).thenReturn(0);

        // Executar o cálculo
        CharacterAttribute finalAttributes = calculatorService.calculateFinalAttributes(baseAttributes, equipment);

        // Verificar se os atributos finais estão corretos
        assertEquals(15, finalAttributes.getStr(), "STR final deve ser 10 + 5 = 15");
        assertEquals(12, finalAttributes.getDex(), "DEX final deve ser 12 + 0 = 12");
        assertEquals(17, finalAttributes.getCon(), "CON final deve ser 15 + 2 = 17");
        assertEquals(7, finalAttributes.getWil(), "WIL final deve ser 8 - 1 = 7");
        assertEquals(14, finalAttributes.getMnd(), "MND final deve ser 11 + 3 = 14");
        assertEquals(14, finalAttributes.getSpi(), "SPI final deve ser 14 + 0 = 14");
    }

    @Test
    void calculateFinalAttributes_shouldReturnBaseAttributes_whenNoEquipmentBonus() {
        // Configurar bônus zero no equipamento
        when(equipment.getTotalStrBonus()).thenReturn(0);
        when(equipment.getTotalDexBonus()).thenReturn(0);
        when(equipment.getTotalConBonus()).thenReturn(0);
        when(equipment.getTotalWilBonus()).thenReturn(0);
        when(equipment.getTotalMndBonus()).thenReturn(0);
        when(equipment.getTotalSpiBonus()).thenReturn(0);

        // Executar o cálculo
        CharacterAttribute finalAttributes = calculatorService.calculateFinalAttributes(baseAttributes, equipment);

        // Verificar se os atributos finais são iguais aos base
        assertEquals(baseAttributes.getStr(), finalAttributes.getStr());
        assertEquals(baseAttributes.getDex(), finalAttributes.getDex());
        assertEquals(baseAttributes.getCon(), finalAttributes.getCon());
        assertEquals(baseAttributes.getWil(), finalAttributes.getWil());
        assertEquals(baseAttributes.getMnd(), finalAttributes.getMnd());
        assertEquals(baseAttributes.getSpi(), finalAttributes.getSpi());
    }

    @Test
    void calculateTotalBonus_shouldSumAllEquipmentBonuses() {
        // Configurar bônus do equipamento
        when(equipment.getTotalStrBonus()).thenReturn(5);
        when(equipment.getTotalDexBonus()).thenReturn(1);
        when(equipment.getTotalConBonus()).thenReturn(2);
        when(equipment.getTotalWilBonus()).thenReturn(3);
        when(equipment.getTotalMndBonus()).thenReturn(4);
        when(equipment.getTotalSpiBonus()).thenReturn(5);

        // Executar o cálculo
        int totalBonus = calculatorService.calculateTotalBonus(equipment);

        // Verificar a soma total: 5 + 1 + 2 + 3 + 4 + 5 = 20
        assertEquals(20, totalBonus, "A soma total dos bônus deve ser 20");
    }
}
