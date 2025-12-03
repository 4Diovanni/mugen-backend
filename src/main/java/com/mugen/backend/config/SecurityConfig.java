package com.mugen.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitar CSRF (API REST stateless não precisa)
                .csrf(AbstractHttpConfigurer::disable)

                // Desabilitar CORS (ou configurar depois se precisar)
                .cors(AbstractHttpConfigurer::disable)

                // ✅ PERMITIR TUDO POR ENQUANTO (sem autenticação)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Desabilitar formulário de login padrão
                .formLogin(AbstractHttpConfigurer::disable)

                // Desabilitar HTTP Basic Auth
                .httpBasic(AbstractHttpConfigurer::disable)

                // Desabilitar logout padrão
                .logout(AbstractHttpConfigurer::disable)

                // Stateless session (preparado para JWT no futuro)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Desabilitar frame options (para H2 console se usar)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }
}
