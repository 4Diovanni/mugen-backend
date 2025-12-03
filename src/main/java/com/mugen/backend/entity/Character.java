package com.mugen.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "character")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Character extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(name = "level")
    @Builder.Default
    private Integer level = 1;

    @Column(name = "exp")
    @Builder.Default
    private Long exp = 0L;

    @Column(name = "tp")
    @Builder.Default
    private Integer tp = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_transformation_id")
    private Transformation activeTransformation;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ✅ CORRIGIDO: Adicionado @JsonIgnoreProperties para evitar recursão infinita
    @OneToOne(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"character"})
    private CharacterAttribute attributes;

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CharacterSkill> skills = new HashSet<>();

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CharacterTransformation> transformations = new HashSet<>();

    // Helper methods

    public void setAttributes(CharacterAttribute attributes) {
        if (attributes != null) {
            this.attributes = attributes;
            attributes.setCharacter(this);  // ✅ Garantir relacionamento bidirecional
        }
    }

    public void addSkill(CharacterSkill characterSkill) {
        skills.add(characterSkill);
        characterSkill.setCharacter(this);
    }

    public void removeSkill(CharacterSkill characterSkill) {
        skills.remove(characterSkill);
        characterSkill.setCharacter(null);
    }

    public void addTransformation(CharacterTransformation characterTransformation) {
        transformations.add(characterTransformation);
        characterTransformation.setCharacter(this);
    }

    public void removeTransformation(CharacterTransformation characterTransformation) {
        transformations.remove(characterTransformation);
        characterTransformation.setCharacter(null);
    }
}
