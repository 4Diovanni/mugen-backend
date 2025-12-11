package com.mugen.backend.entity.character;

import com.mugen.backend.entity.BaseEntity;
import com.mugen.backend.entity.Transformation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "character_transformation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterTransformation extends BaseEntity {

    @EmbeddedId
    private CharacterTransformationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("characterId")
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("transformationId")
    @JoinColumn(name = "transformation_id", nullable = false)
    private Transformation transformation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean unlocked = false;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    public void unlock() {
        this.unlocked = true;
        this.unlockedAt = LocalDateTime.now();
    }
}
