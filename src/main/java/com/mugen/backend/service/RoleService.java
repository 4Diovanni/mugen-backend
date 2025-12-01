package com.mugen.backend.service;

import com.mugen.backend.entity.Role;
import com.mugen.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        log.debug("Finding all roles");
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Integer id) {
        log.debug("Finding role by id: {}", id);
        return roleRepository.findById(id);
    }

    public Optional<Role> findByName(String name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name);
    }

    public Optional<Role> findByRoleName(Role.RoleName roleName) {
        return findByName(roleName.name());
    }

    @Transactional
    public Role save(Role role) {
        log.info("Saving role: {}", role.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        Role role = Role.builder()
                .name(name)
                .description(description)
                .build();

        return roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
