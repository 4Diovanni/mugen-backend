package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tp_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TPTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @Column(nullable = false)
    private Integer amount; // Positivo = ganhou, Negativo = gastou

    @Column(nullable = false)
    private Integer balanceAfter; // TP do personagem APÓS a transação

    @Column(nullable = false, length = 50)
    private String transactionType; // ALLOCATION, MINIGAME, MASTER, EVENT, ACHIEVEMENT, etc

    @Column(length = 255, nullable = false)
    private String reason; // Ex: "MINIGAME_REFLEX", "SKILL_PURCHASE", "MASTER_ADJUSTMENT"

    @Column(name = "created_by")
    private UUID createdBy; // ID do usuário que criou (mestre ou sistema)

    // Métodos auxiliares
    public boolean isEarned() {
        return amount != null && amount > 0;
    }

    public boolean isSpent() {
        return amount != null && amount < 0;
    }
}
