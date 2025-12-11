package com.mugen.backend.controller;

import com.mugen.backend.dto.inventory.WeaponDTO;
import com.mugen.backend.service.inventory.WeaponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🔫 WeaponController - Gerenciar Armas do Sistema
 * Rotas:
 * - GET /weapons - Listar todas
 * - GET /weapons/{id} - Buscar por ID
 * - GET /weapons/primary-type/{type} - Filtrar por tipo
 * - GET /weapons/element/{element} - Filtrar por elemento
 * - GET /weapons/rarity/{rarity} - Filtrar por raridade
 * - POST /weapons/validate - Validar requisitos
 */
@Slf4j
@RestController
@RequestMapping("/weapons")
@RequiredArgsConstructor
public class WeaponController {

    private final WeaponService weaponService;

    /**
     * ✅ GET /weapons
     * Listar todas as armas com paginação
     */
    @GetMapping
    public ResponseEntity<Page<WeaponDTO>> getAllWeapons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("📋 GET /weapons - Listando armas (página: {}, tamanho: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<WeaponDTO> weapons = weaponService.getActiveWeapons(pageable);
        log.info("✅ {} armas encontradas", weapons.getTotalElements());
        return ResponseEntity.ok(weapons);
    }

    /**
     * ✅ GET /weapons/{id}
     * Buscar arma por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<WeaponDTO> getWeaponById(@PathVariable Integer id) {
        log.info("🔍 GET /weapons/{} - Buscando arma", id);
        WeaponDTO weapon = weaponService.getWeaponById(id);
        log.info("✅ Arma encontrada: {}", weapon.getName());
        return ResponseEntity.ok(weapon);
    }

    /**
     * ✅ GET /weapons/primary-type/{type}
     * Filtrar armas por tipo primário
     */
    @GetMapping("/primary-type/{type}")
    public ResponseEntity<List<WeaponDTO>> getWeaponsByPrimaryType(@PathVariable String type) {
        log.info("🔍 GET /weapons/primary-type/{} - Filtrando por tipo", type);
        List<WeaponDTO> weapons = weaponService.getWeaponsByPrimaryType(type);
        log.info("✅ {} armas encontradas para tipo: {}", weapons.size(), type);
        return ResponseEntity.ok(weapons);
    }

    /**
     * ✅ GET /weapons/element/{element}
     * Filtrar armas por elemento
     */
    @GetMapping("/element/{element}")
    public ResponseEntity<List<WeaponDTO>> getWeaponsByElement(@PathVariable String element) {
        log.info("🔍 GET /weapons/element/{} - Filtrando por elemento", element);
        List<WeaponDTO> weapons = weaponService.getWeaponsByElement(element);
        log.info("✅ {} armas encontradas para elemento: {}", weapons.size(), element);
        return ResponseEntity.ok(weapons);
    }

    /**
     * ✅ GET /weapons/rarity/{rarity}
     * Filtrar armas por raridade
     */
    @GetMapping("/rarity/{rarity}")
    public ResponseEntity<List<WeaponDTO>> getWeaponsByRarity(@PathVariable String rarity) {
        log.info("🔍 GET /weapons/rarity/{} - Filtrando por raridade", rarity);
        List<WeaponDTO> weapons = weaponService.getWeaponsByRarity(rarity);
        log.info("✅ {} armas encontradas para raridade: {}", weapons.size(), rarity);
        return ResponseEntity.ok(weapons);
    }

    /**
     * ✅ POST /weapons/validate
     * Validar se personagem pode equipar arma
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateWeapon(
            @RequestBody WeaponDTO weapon,
            @RequestParam Integer str,
            @RequestParam Integer dex,
            @RequestParam Integer con,
            @RequestParam Integer wil,
            @RequestParam Integer mnd,
            @RequestParam Integer spi,
            @RequestParam Integer level
    ) {
        log.info("✅ POST /weapons/validate - Validando arma: {}", weapon.getName());
        boolean canEquip = weaponService.canEquipWeapon(weapon, str, dex, con, wil, mnd, spi, level);
        log.info("✅ Resultado validação: {}", canEquip ? "PODE EQUIPAR" : "NÃO PODE EQUIPAR");
        return ResponseEntity.ok(canEquip);
    }
}
