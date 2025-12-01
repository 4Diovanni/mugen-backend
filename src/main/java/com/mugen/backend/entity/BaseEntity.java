package com.mugen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp  // ✅ ADICIONE ISSO
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @UpdateTimestamp  // ✅ ADICIONE ISSO
    private LocalDateTime updatedAt;
}
