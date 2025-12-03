package com.mugen.backend;

import com.mugen.backend.exception.InsufficientTPException;
import com.mugen.backend.exception.InvalidAttributeException;
import com.mugen.backend.exception.MaxAttributeExceededException;
import com.mugen.backend.service.TPService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TPServiceTest {

    private final TPService tpService = new TPService(null, null);

    @Test
    void testCalculateAttributeCost_Tier1() {
        // 1-50: 1 TP por ponto
        int cost = tpService.calculateAttributeCost(10, 5);
        assertEquals(5, cost); // 5 pontos * 1 TP = 5 TP
    }

    @Test
    void testCalculateAttributeCost_Tier2() {
        // 51-80: 2 TP por ponto
        int cost = tpService.calculateAttributeCost(50, 5);
        assertEquals(10, cost); // 5 pontos * 2 TP = 10 TP
    }

    @Test
    void testCalculateAttributeCost_Tier3() {
        // 81-120: 3 TP por ponto
        int cost = tpService.calculateAttributeCost(80, 5);
        assertEquals(15, cost); // 5 pontos * 3 TP = 15 TP
    }

    @Test
    void testCalculateAttributeCost_CrossTiers() {
        // De 48 a 53 (cruza tier 1 e 2)
        // 48->49: 1 TP
        // 49->50: 1 TP
        // 50->51: 2 TP
        // 51->52: 2 TP
        // 52->53: 2 TP
        // Total: 1+1+2+2+2 = 8 TP
        int cost = tpService.calculateAttributeCost(48, 5);
        assertEquals(8, cost);
    }

    @Test
    void testCalculateAttributeCost_ExceedsMax() {
        assertThrows(MaxAttributeExceededException.class, () -> {
            tpService.calculateAttributeCost(118, 5); // Ultrapassaria 120
        });
    }
}
