package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "character_transformation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterTransformation implements Serializable {

    @EmbeddedId
    private CharacterTransformationId id;

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
    @MapsId("transformationId")
    @JoinColumn(name = "transformation_id")
    private Transformation transformation;

    @Column(name = "unlocked")
    @Builder.Default
    private Boolean unlocked = false;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    public void unlock() {
        this.unlocked = true;
        this.unlockedAt = LocalDateTime.now();
    }

    // Embedded ID class
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CharacterTransformationId implements Serializable {
        @Column(name = "character_id", columnDefinition = "UUID")
        private UUID characterId;

        @Column(name = "transformation_id")
        private Integer transformationId;
    }
}
