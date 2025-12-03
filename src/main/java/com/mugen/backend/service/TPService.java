package com.mugen.backend.service;

import com.mugen.backend.dto.AllocateAttributeRequest;
import com.mugen.backend.dto.AwardTPRequest;
import com.mugen.backend.dto.TPSummary;
import com.mugen.backend.entity.Character;
import com.mugen.backend.entity.CharacterAttribute;
import com.mugen.backend.entity.TPTransaction;
import com.mugen.backend.entity.User;
import com.mugen.backend.exception.InsufficientTPException;
import com.mugen.backend.exception.InvalidAttributeException;
import com.mugen.backend.exception.MaxAttributeExceededException;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.TPTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TPService {

    private final TPTransactionRepository tpTransactionRepository;
    private final CharacterRepository characterRepository;

    // Constantes de progressão
    private static final int MAX_ATTRIBUTE_VALUE = 120;
    private static final int TIER_1_LIMIT = 50;  // 1-50: 1 TP por ponto
    private static final int TIER_2_LIMIT = 80;  // 51-80: 2 TP por ponto
    // 81-120: 3 TP por ponto

    /**
     * Aloca pontos em um atributo, gastando TP
     */
    @Transactional
    public Character allocateAttribute(UUID characterId, AllocateAttributeRequest request, User user) {
        log.info("Allocating {} points to {} for character {}",
                request.getPoints(), request.getAttributeName(), characterId);

        // Buscar personagem com atributos
        Character character = characterRepository.findByIdWithAttributes(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        CharacterAttribute attr = character.getAttributes();
        String attrName = request.getAttributeName().toUpperCase();

        // Validar atributo
        if (!isValidAttribute(attrName)) {
            throw new InvalidAttributeException(attrName);
        }

        // Obter valor atual
        int currentValue = getCurrentAttributeValue(attr, attrName);
        int newValue = currentValue + request.getPoints();

        // Validar limite máximo
        if (newValue > MAX_ATTRIBUTE_VALUE) {
            throw new MaxAttributeExceededException(attrName, MAX_ATTRIBUTE_VALUE);
        }

        // Calcular custo
        int cost = calculateAttributeCost(currentValue, request.getPoints());

        // Validar TP suficiente
        if (character.getTp() < cost) {
            throw new InsufficientTPException(cost, character.getTp());
        }

        // Atualizar atributo
        updateAttribute(attr, attrName, newValue);

        // Debitar TP
        character.setTp(character.getTp() - cost);

        // Registrar transação
        TPTransaction transaction = TPTransaction.builder()
                .character(character)
                .amount(-cost)
                .reason(String.format("ATTRIBUTE_%s_+%d", attrName, request.getPoints()))
                .createdBy(user.getId())
                .build();

        tpTransactionRepository.save(transaction);

        Character saved = characterRepository.save(character);

        log.info("Allocated {} points to {}. Cost: {} TP. Remaining TP: {}",
                request.getPoints(), attrName, cost, saved.getTp());

        return saved;
    }

    /**
     * Concede TP ao personagem (por minigame, mestre, evento)
     */
    @Transactional
    public Character awardTP(AwardTPRequest request, User awardedBy) {
        log.info("Awarding {} TP to character {} for reason: {}",
                request.getAmount(), request.getCharacterId(), request.getReason());

        Character character = characterRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + request.getCharacterId()));

        // Adicionar TP
        character.setTp(character.getTp() + request.getAmount());

        // Registrar transação
        TPTransaction transaction = TPTransaction.builder()
                .character(character)
                .amount(request.getAmount())
                .reason(request.getReason())
                .createdBy(awardedBy.getId())
                .build();

        tpTransactionRepository.save(transaction);

        Character saved = characterRepository.save(character);

        log.info("Awarded {} TP to {}. New total: {}",
                request.getAmount(), character.getName(), saved.getTp());

        return saved;
    }

    /**
     * Calcula custo progressivo de alocação de atributos
     * 1-50: 1 TP por ponto
     * 51-80: 2 TP por ponto
     * 81-120: 3 TP por ponto
     */
    public int calculateAttributeCost(int currentValue, int pointsToAllocate) {
        int totalCost = 0;

        for (int i = 0; i < pointsToAllocate; i++) {
            int value = currentValue + i + 1; // Próximo valor

            if (value <= TIER_1_LIMIT) {
                totalCost += 1;
            } else if (value <= TIER_2_LIMIT) {
                totalCost += 2;
            } else if (value <= MAX_ATTRIBUTE_VALUE) {
                totalCost += 3;
            } else {
                throw new MaxAttributeExceededException("Attribute value cannot exceed " + MAX_ATTRIBUTE_VALUE);
            }
        }

        return totalCost;
    }

    /**
     * Obtém histórico de transações de TP
     */
    public List<TPTransaction> getTransactionHistory(UUID characterId) {
        return tpTransactionRepository.findByCharacterIdOrderByCreatedAtDesc(characterId);
    }

    /**
     * Obtém resumo de TP do personagem
     */
    public TPSummary getTPSummary(UUID characterId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + characterId));

        Long totalEarned = tpTransactionRepository.sumEarnedByCharacterId(characterId);
        Long totalSpent = tpTransactionRepository.sumSpentByCharacterId(characterId);

        // Breakdown por categoria (usando pattern matching no reason)
        Long earnedFromMinigames = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "MINIGAME");
        Long earnedFromMaster = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "MASTER");
        Long earnedFromEvents = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "EVENT");

        Long spentOnAttributes = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "ATTRIBUTE");
        Long spentOnSkills = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "SKILL");
        Long spentOnTransformations = tpTransactionRepository.sumByCharacterIdAndReasonPattern(characterId, "TRANSFORMATION");

        return TPSummary.builder()
                .currentTP(character.getTp())
                .totalEarned(totalEarned != null ? totalEarned : 0L)
                .totalSpent(totalSpent != null ? totalSpent : 0L)
                .lifetimeTP(totalEarned != null ? totalEarned : 0L)
                .earnedFromMinigames(earnedFromMinigames != null ? Math.abs(earnedFromMinigames) : 0L)
                .earnedFromMaster(earnedFromMaster != null ? Math.abs(earnedFromMaster) : 0L)
                .earnedFromEvents(earnedFromEvents != null ? Math.abs(earnedFromEvents) : 0L)
                .spentOnAttributes(spentOnAttributes != null ? Math.abs(spentOnAttributes) : 0L)
                .spentOnSkills(spentOnSkills != null ? Math.abs(spentOnSkills) : 0L)
                .spentOnTransformations(spentOnTransformations != null ? Math.abs(spentOnTransformations) : 0L)
                .build();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private boolean isValidAttribute(String attributeName) {
        return List.of("STR", "DEX", "CON", "WIL", "MND", "SPI").contains(attributeName);
    }

    private int getCurrentAttributeValue(CharacterAttribute attr, String attributeName) {
        return switch (attributeName) {
            case "STR" -> attr.getStr();
            case "DEX" -> attr.getDex();
            case "CON" -> attr.getCon();
            case "WIL" -> attr.getWil();
            case "MND" -> attr.getMnd();
            case "SPI" -> attr.getSpi();
            default -> throw new InvalidAttributeException(
                    String.format("Invalid attribute name: %s. Valid attributes are: STR, DEX, CON, WIL, MND, SPI", attributeName)
            );
        };
    }

    private void updateAttribute(CharacterAttribute attr, String attributeName, int newValue) {
        switch (attributeName) {
            case "STR" -> attr.setStr(newValue);
            case "DEX" -> attr.setDex(newValue);
            case "CON" -> attr.setCon(newValue);
            case "WIL" -> attr.setWil(newValue);
            case "MND" -> attr.setMnd(newValue);
            case "SPI" -> attr.setSpi(newValue);
            default -> throw new InvalidAttributeException(
                    String.format("Invalid attribute name: %s. Valid attributes are: STR, DEX, CON, WIL, MND, SPI", attributeName)
            );
        }
    }
}

