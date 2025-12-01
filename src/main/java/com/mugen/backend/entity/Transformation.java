package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transformation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(nullable = false, updatable = false)
//    @CreationTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime updatedAt;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @Column(name = "race_required")
    @Builder.Default
    private Boolean raceRequired = false;

    @Column(name = "required_level")
    @Builder.Default
    private Integer requiredLevel = 0;

    @Column(name = "multiplier", nullable = false, precision = 5, scale = 3)
    private BigDecimal multiplier;

    @Column(name = "ki_drain_per_sec", precision = 6, scale = 3)
    @Builder.Default
    private BigDecimal kiDrainPerSec = BigDecimal.ZERO;

    @Column(name = "tp_cost")
    @Builder.Default
    private Integer tpCost = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
