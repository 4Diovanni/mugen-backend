package com.mugen.backend.config;

import com.mugen.backend.security.JwtAuthenticationFilter;
import com.mugen.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /**
     * 🔒 Configuração de CORS
     * Define origens permitidas, métodos HTTP e headers
     * ✅ ATUALIZADO: Incluindo localhost:5173 (Vite dev server)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens permitidas
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // Frontend local (Create React App)
                "http://localhost:3001",      // Frontend alternativo
                "http://localhost:5173",      // ✅ Vite dev server (padrão)
                "http://localhost:5174",      // ✅ Vite dev server (alternativo)
                "http://127.0.0.1:3000",      // IPv4 loopback
                "http://127.0.0.1:5173",      // IPv4 Vite
                "https://seu-dominio.com"     // Produção - ALTERAR!
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList(
                "*",
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Accept-Language",
                "Content-Language"
        ));

        // Headers expostos para o cliente
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"
        ));

        // Permitir credenciais (cookies, auth headers)
        config.setAllowCredentials(true);

        // Tempo de cache do preflight (em segundos = 1 hora)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ Habilitar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ Desabilitar CSRF (seguro com JWT)
                .csrf(csrf -> csrf.disable())

                // ✅ Política de sessão
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Autorização de endpoints
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/races").permitAll()
                        .requestMatchers(HttpMethod.GET, "/races/**").permitAll()

                        // Endpoints que requerem autenticação
                        .requestMatchers(HttpMethod.GET, "/characters").authenticated()
                        .requestMatchers(HttpMethod.POST, "/characters").authenticated()
                        .requestMatchers(HttpMethod.GET, "/characters/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/characters/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/characters/**").authenticated()

                        // Endpoints admin (usar @PreAuthorize no controller)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Qualquer outro endpoint requer autenticação
                        .anyRequest().authenticated()
                )

                // ✅ Adicionar filtro JWT
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
