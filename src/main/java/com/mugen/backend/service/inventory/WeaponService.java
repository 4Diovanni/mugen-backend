package com.mugen.backend.service.inventory;

import com.mugen.backend.dto.inventory.WeaponDTO;
import com.mugen.backend.dto.inventory.WeaponRequirementsDTO;
import com.mugen.backend.enums.WeaponPrimaryType;
import com.mugen.backend.enums.ElementalType;
import com.mugen.backend.entity.inventory.Weapon;
import com.mugen.backend.entity.inventory.WeaponRequirements;
import com.mugen.backend.repository.inventory.WeaponRepository;
import com.mugen.backend.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeaponService {

    private final WeaponRepository weaponRepository;

    /**
     * 1️⃣ Obter arma por ID
     * ✅ Com validação NULL
     */
    public WeaponDTO getWeaponById(Integer weaponId) {
        // ✅ Validação: ID não pode ser null
        if (weaponId == null) {
            log.warn("❌ WeaponID inválido: null");
            throw new InvalidOperationException("ID da arma não pode ser nulo");
        }

        log.debug("🔍 Buscando arma com ID: {}", weaponId);

        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("❌ Arma não encontrada com ID: %d", weaponId)
                ));

        return convertToDTO(weapon);
    }

    /**
     * 2️⃣ Listar armas ativas (paginado)
     * ✅ Com validação NULL
     */
    public Page<WeaponDTO> getActiveWeapons(Pageable pageable) {
        // ✅ Validação: Pageable não pode ser null
        Objects.requireNonNull(pageable, "Pageable não pode ser nulo");

        log.debug("📋 Listando armas ativas");

        return weaponRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * 3️⃣ Filtrar armas por tipo primário
     * ✅ Com validação NULL e de string vazia
     */
    public List<WeaponDTO> getWeaponsByPrimaryType(String primaryType) {
        // ✅ Validação
        if (primaryType == null || primaryType.trim().isEmpty()) {
            log.warn("❌ Tipo primário inválido: {}", primaryType);
            throw new InvalidOperationException("Tipo de arma não pode ser vazio");
        }

        log.debug("🔍 Buscando armas com tipo primário: {}", primaryType);

        List<Weapon> weapons = weaponRepository.findAll().stream().filter(w -> w.getPrimaryType().equals(primaryType)).toList();

        // ✅ Log: Pode retornar lista vazia
        if (weapons.isEmpty()) {
            log.info("⚠️  Nenhuma arma encontrada para tipo: {}", primaryType);
        }

        return weapons.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 4️⃣ Filtrar armas por elemento
     * ✅ Com validação NULL
     */
    public List<WeaponDTO> getWeaponsByElement(String element) {
        // ✅ Validação
        if (element == null || element.trim().isEmpty()) {
            log.warn("❌ Elemento inválido: {}", element);
            throw new InvalidOperationException("Elemento não pode ser vazio");
        }

        log.debug("🔍 Buscando armas com elemento: {}", element);

        List<Weapon> weapons = weaponRepository.findAll().stream().filter(w -> w.getElementalType().name().equals(element)).toList();

        // ✅ Log: Pode retornar lista vazia
        if (weapons.isEmpty()) {
            log.info("⚠️  Nenhuma arma encontrada para elemento: {}", element);
        }

        return weapons.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 5️⃣ Filtrar armas por raridade
     * ✅ Com validação NULL
     */
    public List<WeaponDTO> getWeaponsByRarity(String rarity) {
        // ✅ Validação
        if (rarity == null || rarity.trim().isEmpty()) {
            log.warn("❌ Raridade inválida: {}", rarity);
            throw new InvalidOperationException("Raridade não pode ser vazia");
        }

        log.debug("🔍 Buscando armas com raridade: {}", rarity);

        List<Weapon> weapons = weaponRepository.findAll().stream().filter(w -> w.getRarity().name().equals(rarity)).toList();

        // ✅ Log: Pode retornar lista vazia
        if (weapons.isEmpty()) {
            log.info("⚠️  Nenhuma arma encontrada para raridade: {}", rarity);
        }

        return weapons.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 6️⃣ Validar se personagem pode equipar arma
     * ✅ Com validação de NULL em todos os parâmetros
     */
    public boolean canEquipWeapon(WeaponDTO weapon,
                                  Integer str, Integer dex, Integer con,
                                  Integer wil, Integer mnd, Integer spi,
                                  Integer level) {

        // ✅ Validação: weapon não pode ser null
        if (weapon == null) {
            log.warn("❌ Validação de arma null");
            return false;
        }

        // ✅ Validação: stats não podem ser null
        if (str == null || dex == null || con == null ||
                wil == null || mnd == null || spi == null || level == null) {
            log.warn("❌ Um ou mais atributos nulos para validação de arma");
            return false;
        }

        log.debug("✅ Validando requisitos da arma: {}", weapon.getName());

        WeaponRequirementsDTO req = weapon.getRequirements();

        // ✅ Se não tem requisitos, qualquer um pode equipar
        if (req == null) {
            log.debug("✅ Arma sem requisitos");
            return true;
        }

        boolean canEquip =
                (req.getMinStr() == null || str >= req.getMinStr()) &&
                        (req.getMinDex() == null || dex >= req.getMinDex()) &&
                        (req.getMinCon() == null || con >= req.getMinCon()) &&
                        (req.getMinWil() == null || wil >= req.getMinWil()) &&
                        (req.getMinMnd() == null || mnd >= req.getMinMnd()) &&
                        (req.getMinSpi() == null || spi >= req.getMinSpi()) &&
                        (req.getMinLevel() == null || level >= req.getMinLevel());

        log.debug("✅ Resultado validação: {}", canEquip);
        return canEquip;
    }

    /**
     * 7️⃣ Converter Entity para DTO
     * ✅ Com validação NULL
     */
    public WeaponDTO convertToDTO(Weapon weapon) {
        // ✅ Validação: weapon não pode ser null
        if (weapon == null) {
            log.warn("❌ Tentativa de converter Weapon null para DTO");
            return null;
        }

        WeaponRequirementsDTO reqDTO = null;
        if (weapon.getRequirements() != null) {
            reqDTO = convertRequirementsToDTO(weapon.getRequirements());
        }

        // ✅ NULL-SAFE: tpCost pode ser null
        Long sellPrice = weapon.getTpCost() != null
                ? (weapon.getTpCost() * 50) / 100
                : 0L;

        String reqDescription = buildRequirementsDescription(weapon.getRequirements());
        String displayName = buildDisplayName(weapon.getName(), weapon.getRarity().name());

        return WeaponDTO.builder()
                .id(weapon.getId())
                .name(weapon.getName())
                .description(weapon.getDescription())
                .notes(weapon.getNotes())
                .imageUrl(weapon.getImageUrl())
                .primaryType(weapon.getPrimaryType().name()) // modificação
                .secondaryType(weapon.getSecondaryType().name())
                .elementalType(weapon.getElementalType().name())
                .rarity(weapon.getRarity().name())
                .rarityIcon(getRarityIcon(weapon.getRarity().name()))
                .rarityColor(getRarityColor(weapon.getRarity().name()))
                .tpCost(weapon.getTpCost())
                .sellPrice(sellPrice)
                .strBonus(weapon.getStrBonus())
                .dexBonus(weapon.getDexBonus())
                .conBonus(weapon.getConBonus())
                .wilBonus(weapon.getWilBonus())
                .mndBonus(weapon.getMndBonus())
                .spiBonus(weapon.getSpiBonus())
                .requirements(reqDTO)
                .isUnique(weapon.getIsUnique())
                .maxQuantity(weapon.getMaxQuantity())
                .isActive(weapon.getIsActive())
                .displayName(displayName)
                .bonusDescription(buildBonusDescription(weapon))
                .build();
    }

    /**
     * 8️⃣ Converter Requirements para DTO
     * ✅ Com validação NULL
     */
    private WeaponRequirementsDTO convertRequirementsToDTO(WeaponRequirements req) {
        // ✅ Validação
        if (req == null) {
            return null;
        }

        return WeaponRequirementsDTO.builder()
                .minStr(req.getMinStr())
                .minDex(req.getMinDex())
                .minCon(req.getMinCon())
                .minWil(req.getMinWil())
                .minMnd(req.getMinMnd())
                .minSpi(req.getMinSpi())
                .minLevel(req.getMinLevel())
                .description(buildRequirementsDescription(req))
                .build();
    }

    // ==================== HELPER METHODS ====================

    private String buildRequirementsDescription(WeaponRequirements req) {
        if (req == null) {
            return "Sem requisitos";
        }

        StringBuilder sb = new StringBuilder();

        if (req.getMinLevel() != null && req.getMinLevel() > 0) {
            sb.append("Nível ").append(req.getMinLevel()).append(" | ");
        }
        if (req.getMinStr() != null && req.getMinStr() > 0) {
            sb.append("STR ").append(req.getMinStr()).append(" | ");
        }
        if (req.getMinDex() != null && req.getMinDex() > 0) {
            sb.append("DEX ").append(req.getMinDex()).append(" | ");
        }
        if (req.getMinCon() != null && req.getMinCon() > 0) {
            sb.append("CON ").append(req.getMinCon()).append(" | ");
        }
        if (req.getMinWil() != null && req.getMinWil() > 0) {
            sb.append("WIL ").append(req.getMinWil()).append(" | ");
        }
        if (req.getMinMnd() != null && req.getMinMnd() > 0) {
            sb.append("MND ").append(req.getMinMnd()).append(" | ");
        }
        if (req.getMinSpi() != null && req.getMinSpi() > 0) {
            sb.append("SPI ").append(req.getMinSpi()).append(" | ");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 3);  // Remove último " | "
        }

        return sb.toString().isEmpty() ? "Sem requisitos" : sb.toString();
    }

    private String buildBonusDescription(Weapon weapon) {
        StringBuilder sb = new StringBuilder();

        if (weapon.getStrBonus() != null && weapon.getStrBonus() > 0) {
            sb.append("+").append(weapon.getStrBonus()).append(" STR ");
        }
        if (weapon.getDexBonus() != null && weapon.getDexBonus() > 0) {
            sb.append("+").append(weapon.getDexBonus()).append(" DEX ");
        }
        if (weapon.getConBonus() != null && weapon.getConBonus() > 0) {
            sb.append("+").append(weapon.getConBonus()).append(" CON ");
        }
        if (weapon.getWilBonus() != null && weapon.getWilBonus() > 0) {
            sb.append("+").append(weapon.getWilBonus()).append(" WIL ");
        }
        if (weapon.getMndBonus() != null && weapon.getMndBonus() > 0) {
            sb.append("+").append(weapon.getMndBonus()).append(" MND ");
        }
        if (weapon.getSpiBonus() != null && weapon.getSpiBonus() > 0) {
            sb.append("+").append(weapon.getSpiBonus()).append(" SPI ");
        }

        return sb.toString().trim().isEmpty() ? "Sem bônus" : sb.toString().trim();
    }

    private String buildDisplayName(String name, String rarity) {
        // ✅ NULL-SAFE
        String rarityIcon = getRarityIcon(rarity != null ? rarity : "");
        return rarityIcon + " " + (name != null ? name : "Desconhecida");
    }

    private String getRarityIcon(String rarity) {
        if (rarity == null) {
            return "•";
        }

        return switch (rarity) {
            case "LENDARIO" -> "🌟";
            case "EPICO" -> "⭐";
            case "RARO" -> "✨";
            case "INCOMUM" -> "💫";
            default -> "•";
        };
    }

    private String getRarityColor(String rarity) {
        if (rarity == null) {
            return "#ECF0F1";
        }

        return switch (rarity) {
            case "LENDARIO" -> "#FFD700";
            case "EPICO" -> "#9B59B6";
            case "RARO" -> "#3498DB";
            case "INCOMUM" -> "#95A5A6";
            default -> "#ECF0F1";
        };
    }
}
