package com.mugen.backend.service;

import com.mugen.backend.dto.achievement.AwardTPRequest;
import com.mugen.backend.dto.tp.AllocateAttributeRequest;
import com.mugen.backend.dto.tp.TPSummary;
import com.mugen.backend.entity.TPTransaction;
import com.mugen.backend.entity.User;
import com.mugen.backend.entity.character.Character;
import com.mugen.backend.entity.character.CharacterAttribute;
import com.mugen.backend.enums.TPTransactionType;
import com.mugen.backend.exception.InsufficientTPException;
import com.mugen.backend.exception.InvalidAttributeException;
import com.mugen.backend.exception.MaxAttributeExceededException;
import com.mugen.backend.repository.CharacterRepository;
import com.mugen.backend.repository.TPTransactionRepository;
import com.mugen.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final UserRepository userRepository;

    // Constantes de progressão
    private static final int MAX_ATTRIBUTE_VALUE = 120;
    private static final int TIER_1_LIMIT = 50;  // 1-50: 1 TP por ponto
    private static final int TIER_2_LIMIT = 80;  // 51-80: 2 TP por ponto
    // 81-120: 3 TP por ponto

    /**
     * Aloca pontos num atributo, gastando TP
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

        UUID createdById = null;
        if (user != null && user.getId() != null) {
            boolean userExists = userRepository.existsById(user.getId());
            if (userExists) {
                createdById = user.getId();
            } else {
                log.warn("User {} does not exist in app_user table. Setting createdBy to NULL", user.getId());
            }
        }

        // Registrar transação
        TPTransaction transaction = TPTransaction.builder()
                .character(character)
                .amount(-cost)
                .balanceAfter(character.getTp())  // Saldo APÓS debitar
                .transactionType(TPTransactionType.ALLOCATION.toString())  // Tipo de transação
                .reason(String.format("ATTRIBUTE_%s_+%d", attrName, request.getPoints()))
                .createdBy(createdById)
                .build();

        tpTransactionRepository.save(transaction);

        Character saved = characterRepository.save(character);

        log.info("Allocated {} points to {}. Cost: {} TP. Remaining TP: {}",
                request.getPoints(), attrName, cost, saved.getTp());

        return saved;
    }

    /**
     * Concede TP ao personagem (por minigame, mestre, evento)
     * Transação separada com REQUIRES_NEW e null handling
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Character awardTP(AwardTPRequest request, User awardedBy) {
        log.info("Awarding {} TP to character {} for reason: {}",
                request.getAmount(), request.getCharacterId(), request.getReason());

        Character character = characterRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new IllegalArgumentException("Character not found: " + request.getCharacterId()));

        // Adicionar TP
        character.setTp(character.getTp() + request.getAmount());

        // Tratar null em awardedBy
        // UUID userId = awardedBy != null ? awardedBy.getId() : null;

        UUID createdById = null;
        if (awardedBy != null && awardedBy.getId() != null) {
            boolean userExists = userRepository.existsById(awardedBy.getId());
            if (userExists) {
                createdById = awardedBy.getId();
            } else {
                // Tentar buscar por email
                if (awardedBy.getEmail() != null) {
                    var userByEmail = userRepository.findByEmail(awardedBy.getEmail());
                    if (userByEmail.isPresent()) {
                        createdById = userByEmail.get().getId();
                        log.info("Found user by email: {}", createdById);
                    }
                }
            }
        }

        // Registrar transação COM o novo saldo
        TPTransaction transaction = TPTransaction.builder()
                .character(character)
                .amount(request.getAmount())
                .balanceAfter(character.getTp()) // Saldo APÓS ganhar
                .transactionType(extractTransactionType(request.getReason())) // Tipo extraído da razão
                .reason(request.getReason())
                .createdBy(createdById)  // Pode ser null (transação do sistema)
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

    /**
     * Valida se o nome do atributo é válido
     */
    private boolean isValidAttribute(String attributeName) {
        return List.of("STR", "DEX", "CON", "WIL", "MND", "SPI").contains(attributeName);
    }

    /**
     * Obtém valor atual do atributo
     */
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

    /**
     * Atualiza valor do atributo
     */
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

    /**
     * Extrai tipo de transação a partir da razão
     * Usado para categorizar transações de TP
     */
    private String extractTransactionType(String reason) {
        if (reason.startsWith("MINIGAME")) return TPTransactionType.MINIGAME.toString();
        if (reason.startsWith("MASTER")) return TPTransactionType.MASTER.toString();
        if (reason.startsWith("EVENT")) return TPTransactionType.EVENT.toString();
        if (reason.startsWith("ACHIEVEMENT")) return TPTransactionType.ACHIEVEMENT.toString();
        if (reason.startsWith("SKILL")) return TPTransactionType.SKILL.toString();
        if (reason.startsWith("TRANSFORMATION")) return TPTransactionType.TRANSFORMATION.toString();
        return TPTransactionType.ALLOCATION.toString();
    }
}