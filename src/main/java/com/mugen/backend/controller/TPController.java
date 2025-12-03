package com.mugen.backend.controller;

import com.mugen.backend.dto.AwardTPRequest;
import com.mugen.backend.dto.TPSummary;
import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.TPTransaction;
import com.mugen.backend.entity.User;
import com.mugen.backend.service.TPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tp")
@RequiredArgsConstructor
@Slf4j
public class TPController {

    private final TPService tpService;

    /**
     * POST /tp/award
     * Conceder TP ao personagem (mestre/sistema)
     */
    @PostMapping("/award")
    public ResponseEntity<Character> awardTP(@RequestBody @Valid AwardTPRequest request) {
        log.info("Awarding {} TP to character {} for {}",
                request.getAmount(), request.getCharacterId(), request.getReason());

        // TODO: Pegar user do contexto de segurança
        User mockUser = User.builder().id(UUID.randomUUID()).build();

        Character updated = tpService.awardTP(request, mockUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /tp/history/{characterId}
     * Histórico de transações de TP
     */
    @GetMapping("/history/{characterId}")
    public ResponseEntity<List<TPTransaction>> getHistory(@PathVariable UUID characterId) {
        log.debug("Getting TP history for character: {}", characterId);

        List<TPTransaction> history = tpService.getTransactionHistory(characterId);
        return ResponseEntity.ok(history);
    }

    /**
     * GET /api/tp/summary/{characterId}
     * Resumo de TP (ganhos, gastos, breakdown)
     */
    @GetMapping("/summary/{characterId}")
    public ResponseEntity<TPSummary> getSummary(@PathVariable UUID characterId) {
        log.debug("Getting TP summary for character: {}", characterId);

        TPSummary summary = tpService.getTPSummary(characterId);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/tp/calculate-cost
     * Calcular custo de alocação de atributos (antes de confirmar)
     */
    @GetMapping("/calculate-cost")
    public ResponseEntity<Integer> calculateCost(
            @RequestParam Integer currentValue,
            @RequestParam Integer pointsToAllocate) {

        log.debug("Calculating cost: current={}, points={}", currentValue, pointsToAllocate);

        int cost = tpService.calculateAttributeCost(currentValue, pointsToAllocate);
        return ResponseEntity.ok(cost);
    }
}
