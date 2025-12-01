package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "character_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterSkill implements Serializable {

    @EmbeddedId
    private CharacterSkillId id;

//    @Column(nullable = false, updatable = false)
//    @CreationTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("characterId")
    @JoinColumn(name = "character_id")
    private Character character;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "current_level")
    @Builder.Default
    private Integer currentLevel = 1;

    @Column(name = "learned_at")
    private LocalDateTime learnedAt;

    @PrePersist
    protected void onCreate() {
        learnedAt = LocalDateTime.now();
    }

    // Embedded ID class
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CharacterSkillId implements Serializable {
        @Column(name = "character_id", columnDefinition = "UUID")
        private UUID characterId;

        @Column(name = "skill_id")
        private Integer skillId;
    }
}
