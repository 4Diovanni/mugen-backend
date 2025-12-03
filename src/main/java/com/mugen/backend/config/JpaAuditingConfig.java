package com.mugen.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Habilita auditing automático do Spring Data JPA
    // @CreatedDate e @LastModifiedDate agora funcionam!
}
