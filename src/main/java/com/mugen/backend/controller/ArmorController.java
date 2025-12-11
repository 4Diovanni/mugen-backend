package com.mugen.backend.controller;

import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.service.inventory.ArmorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🛡️ ArmorController - Gerenciar Armaduras do Sistema
 * Rotas:
 * - GET /armors - Listar todas
 * - GET /armors/{id} - Buscar por ID
 * - GET /armors/type/{type} - Filtrar por tipo
 * - GET /armors/rarity/{rarity} - Filtrar por raridade
 * - POST /armors/validate - Validar requisitos
 */
@Slf4j
@RestController
@RequestMapping("/armors")
@RequiredArgsConstructor
public class ArmorController {

    private final ArmorService armorService;

    /**
     * ✅ GET /armors
     * Listar todas as armaduras com paginação
     */
    @GetMapping
    public ResponseEntity<Page<ArmorDTO>> getAllArmors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("📋 GET /armors - Listando armaduras (página: {}, tamanho: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ArmorDTO> armors = armorService.getActiveArmors(pageable);
        log.info("✅ {} armaduras encontradas", armors.getTotalElements());
        return ResponseEntity.ok(armors);
    }

    /**
     * ✅ GET /armors/{id}
     * Buscar armadura por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArmorDTO> getArmorById(@PathVariable Integer id) {
        log.info("🔍 GET /armors/{} - Buscando armadura", id);
        ArmorDTO armor = armorService.getArmorById(id);
        log.info("✅ Armadura encontrada: {}", armor.getName());
        return ResponseEntity.ok(armor);
    }

    /**
     * ✅ GET /armors/type/{type}
     * Filtrar armaduras por tipo (LIGHT, HEAVY, etc.)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ArmorDTO>> getArmorsByType(@PathVariable String type) {
        log.info("🔍 GET /armors/type/{} - Filtrando por tipo", type);
        List<ArmorDTO> armors = armorService.getArmorsByType(type);
        log.info("✅ {} armaduras encontradas para tipo: {}", armors.size(), type);
        return ResponseEntity.ok(armors);
    }

    /**
     * ✅ GET /armors/rarity/{rarity}
     * Filtrar armaduras por raridade
     */
    @GetMapping("/rarity/{rarity}")
    public ResponseEntity<List<ArmorDTO>> getArmorsByRarity(@PathVariable String rarity) {
        log.info("🔍 GET /armors/rarity/{} - Filtrando por raridade", rarity);
        List<ArmorDTO> armors = armorService.getArmorsByRarity(rarity);
        log.info("✅ {} armaduras encontradas para raridade: {}", armors.size(), rarity);
        return ResponseEntity.ok(armors);
    }

    /**
     * ✅ POST /armors/validate
     * Validar se personagem pode equipar armadura
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateArmor(
            @Valid @RequestBody ArmorDTO armor,  // ✅ ADICIONAR @Valid
            @RequestParam Integer con,
            @RequestParam Integer level
    ) {
        log.info("✅ POST /armors/validate - Validando armadura: {}", armor.getName());

        // ✅ Validação extra (defensive programming)
        if (armor == null || armor.getMinLevel() == null || armor.getMinCon() == null) {
            log.warn("Requisição inválida: armadura ou requisitos nulos");
            return ResponseEntity.badRequest().build();
        }

        if (level == null || con == null) {
            log.warn("Requisição inválida: atributos do player nulos");
            return ResponseEntity.badRequest().build();
        }

        boolean canEquip = armorService.canEquipArmor(armor, level, con);
        log.info("✅ Resultado validação: {}", canEquip ? "PODE EQUIPAR" : "NÃO PODE EQUIPAR");
        return ResponseEntity.ok(canEquip);
    }
}