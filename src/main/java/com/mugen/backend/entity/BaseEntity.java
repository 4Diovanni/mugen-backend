package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Column(name = "created_at", nullable = false, updatable = false) // ✅ CORRIGIDO: snake_case
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // ✅ CORRIGIDO: snake_case
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
