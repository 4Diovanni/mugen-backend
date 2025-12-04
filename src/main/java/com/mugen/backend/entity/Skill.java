package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 50)
    private SkillType skillType;

    @Column(name = "base_tp_cost")
    @Builder.Default
    private Integer baseTpCost = 10;

    @Column(name = "required_level")
    @Builder.Default
    private Integer requiredLevel = 1;

    @Column(name = "max_level")
    @Builder.Default
    private Integer maxLevel = 10;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    public enum SkillType {
        ACTIVE, PASSIVE, RACIAL, ULTIMATE
    }
}
