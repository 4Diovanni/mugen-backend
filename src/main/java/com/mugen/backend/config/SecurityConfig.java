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
                // Desabilitar CSRF (vamos implementar proteção JWT depois)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar autorização de requisições
                .authorizeHttpRequests(auth -> auth
                        // Permitir acesso SEM autenticação aos endpoints de health
                        .requestMatchers(
                                "/health/**",
                                "/actuator/**",
                                "/error"
                        ).permitAll()

                        // Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                )

                // Desabilitar formulário de login padrão (vamos usar JWT)
                .formLogin(AbstractHttpConfigurer::disable)

                // Desabilitar HTTP Basic Auth
                .httpBasic(AbstractHttpConfigurer::disable)

                // Stateless session (não cria sessão, usa JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
