package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.LocalDateTime;

/**
 * Entity que define um Achievement/Conquista
 * Pode ser de diferentes categorias: COMBAT, EXPLORATION, SKILL, SOCIAL, etc
 */
@Entity
@Table(name = "achievement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "key_name", unique = true, nullable = false, length = 120)
    private String keyName; // identificador único: "first_character", "level_50", etc

    @Column(nullable = false, length = 150)
    private String title; // "Primeiro Personagem"

    @Column(columnDefinition = "TEXT")
    private String description; // "Crie seu primeiro personagem"

    @Column(name = "requirement_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)  // ✅ Correto!
    private String requirementJson;

    @Column(name = "reward_tp", nullable = false)
    private Integer rewardTp; // TP dado ao obter achievement

    @Column(length = 50)
    private String category; // SPECIAL, COMBAT, EXPLORATION, SKILL, SOCIAL

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}