package com.mugen.backend.entity.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mugen.backend.entity.BaseEntity;
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

    // ✅ O ID é o mesmo do Character (compartilhado)
    @Id
    @Column(name = "character_id")
    private UUID characterId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // ✅ Usa o ID do Character como ID do CharacterAttribute
    @JoinColumn(name = "character_id")
    @JsonIgnoreProperties({"attributes"})
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
