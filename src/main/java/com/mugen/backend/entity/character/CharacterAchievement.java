package com.mugen.backend.entity.character;

import com.mugen.backend.entity.Achievement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity que mapeia achievements obtidos por um personagem
 * Chave composta: character_id + achievement_id
 */
@Entity
@Table(name = "character_achievement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime obtainedAt;

    @Column(columnDefinition = "TEXT")
    private String notificationMessage; // "Parabéns! Você desbloqueou: Primeiro Personagem"
}
