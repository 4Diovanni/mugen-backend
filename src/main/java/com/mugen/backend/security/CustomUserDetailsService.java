package com.mugen.backend.security;

import com.mugen.backend.entity.User;
import com.mugen.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * CustomUserDetailsService
 * Implementação do UserDetailsService do Spring Security
 * Responsável por carregar os dados do usuário do banco de dados
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carrega um usuário pelo username (email)
     * @param username (email do usuário)
     * @return UserDetails com informações do usuário e a suas roles/permissões
     * @throws UsernameNotFoundException se usuário não encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuário no banco de dados pelo email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com email: " + username
                ));

        // Converter roles do usuário em GrantedAuthority
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()))
        );

        // Retornar UserDetails com dados do usuário
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}