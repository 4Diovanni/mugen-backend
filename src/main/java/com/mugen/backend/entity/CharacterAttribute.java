package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "character_attribute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterAttribute extends BaseEntity {

    @Id
    @Column(name = "character_id", columnDefinition = "UUID")
    private UUID characterId;

//    @Column(nullable = false, updatable = false)
//    @CreationTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "character_id")
    private Character character;

    @Column(name = "str")
    @Builder.Default
    private Integer str = 0;

    @Column(name = "dex")
    @Builder.Default
    private Integer dex = 0;

    @Column(name = "con")
    @Builder.Default
    private Integer con = 0;

    @Column(name = "wil")
    @Builder.Default
    private Integer wil = 0;

    @Column(name = "mnd")
    @Builder.Default
    private Integer mnd = 0;

    @Column(name = "spi")
    @Builder.Default
    private Integer spi = 0;

    // Validation
    public boolean isValidAttributeValue(Integer value) {
        return value >= 0 && value <= 120;
    }

    public Integer getTotalPoints() {
        return str + dex + con + wil + mnd + spi;
    }
}
