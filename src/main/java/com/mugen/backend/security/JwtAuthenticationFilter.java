package com.mugen.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter - Processa tokens JWT e popula SecurityContext
 * Funciona com Spring Security 6+ e Jakarta EE
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1️⃣ Extrair Authorization header
            String authHeader = request.getHeader("Authorization");
            log.debug("Auth header: {}", authHeader != null ? "present" : "missing");

            // 2️⃣ Validar formato Bearer Token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No Bearer token found, continuing without authentication");
                filterChain.doFilter(request, response);
                return;
            }

            // 3️⃣ Extrair token (remove "Bearer " prefix e trim)
            String token = authHeader.substring(7).trim();
            log.debug("Token extracted, validating...");

            // 4️⃣ Validar integridade e validade do token
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("Invalid or expired JWT token received");
                filterChain.doFilter(request, response);
                return;
            }

            // 5️⃣ Extrair dados do token (userId e roles)
            String userId = jwtTokenProvider.extractUserId(token);
            Set<String> roles = jwtTokenProvider.extractRoles(token);
            log.info("✅ JWT validated for user: {} with roles: {}", userId, roles);

            // 6️⃣ Converter roles para GrantedAuthority (formato esperado pelo Spring)
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 7️⃣ Criar objeto de autenticação
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,           // principal (usuário)
                            null,             // credenciais (null para token)
                            authorities       // permissões
                    );
            authentication.setDetails(userId);

            // 8️⃣ Setar autenticação no SecurityContext (CRÍTICO!)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("✅ Authentication set in SecurityContext for user: {}", userId);

        } catch (Exception e) {
            log.error("❌ Error processing JWT token: {}", e.getMessage(), e);
            // Continua sem autenticação em caso de erro
        }

        // Continuar cadeia de filtros
        filterChain.doFilter(request, response);
    }
}
