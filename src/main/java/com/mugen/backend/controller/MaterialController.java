package com.mugen.backend.controller;

import com.mugen.backend.dto.inventory.MaterialDTO;
import com.mugen.backend.service.inventory.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 📦 MaterialController - Gerenciar Materiais do Sistema
 * Rotas:
 * - GET /materials - Listar todos
 * - GET /materials/{id} - Buscar por ID
 * - GET /materials/search - Buscar por nome
 */
@Slf4j
@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    /**
     * ✅ GET /materials
     * Listar todos os materiais com paginação
     */
    @GetMapping
    public ResponseEntity<Page<MaterialDTO>> getAllMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("📋 GET /materials - Listando materiais (página: {}, tamanho: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<MaterialDTO> materials = materialService.getActiveMaterials(pageable);
        log.info("✅ {} materiais encontrados", materials.getTotalElements());
        return ResponseEntity.ok(materials);
    }

    /**
     * ✅ GET /materials/{id}
     * Buscar material por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> getMaterialById(@PathVariable Integer id) {
        log.info("🔍 GET /materials/{} - Buscando material", id);
        MaterialDTO material = materialService.getMaterialById(id);
        log.info("✅ Material encontrado: {}", material.getName());
        return ResponseEntity.ok(material);
    }

}
