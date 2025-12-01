package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "race")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(nullable = false, updatable = false)
//    @CreationTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp  // ✅ ADICIONE ISSO
//    private LocalDateTime updatedAt;

    @Column(name = "name", unique = true, nullable = false, length = 80)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_str")
    @Builder.Default
    private Integer startStr = 10;

    @Column(name = "start_dex")
    @Builder.Default
    private Integer startDex = 10;

    @Column(name = "start_con")
    @Builder.Default
    private Integer startCon = 10;

    @Column(name = "start_wil")
    @Builder.Default
    private Integer startWil = 10;

    @Column(name = "start_mnd")
    @Builder.Default
    private Integer startMnd = 10;

    @Column(name = "start_spi")
    @Builder.Default
    private Integer startSpi = 10;

    @Column(name = "race_class_modifier", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal raceClassModifier = BigDecimal.ONE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
