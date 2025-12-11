package com.mugen.backend.service.inventory;

import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.enums.ArmorRarity;
import com.mugen.backend.enums.ArmorType;
import com.mugen.backend.entity.inventory.Armor;
import com.mugen.backend.exception.InvalidOperationException;
import com.mugen.backend.repository.inventory.ArmorRepository;
import com.mugen.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service para gerenciar Armaduras
 * 
 * Responsável por:
 * - Buscar e filtrar armaduras
 * - Validar requisitos de armaduras
 * - Conversão Entity ↔ DTO
 * - Cálculos de preço e bônus
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArmorService {
    
    private final ArmorRepository armorRepository;
    
    /**
     * Obter armadura por ID
     */
    public ArmorDTO getArmorById(Integer armorId) {
        log.debug("Buscando armadura com ID: {}", armorId);
        if (armorId == null) {
            throw new InvalidOperationException("ID da armadura não pode ser nulo");
        }
        Armor armor = armorRepository.findById(armorId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Armadura não encontrada com ID: " + armorId
            ));
        
        return convertToDTO(armor);
    }
    
    /**
     * Listar todas as armaduras ativas
     */
    public Page<ArmorDTO> getActiveArmors(Pageable pageable) {
        log.debug("Listando armaduras ativas");
        Objects.requireNonNull(pageable, "Pageable não pode ser nulo");
        return armorRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    /**
     * Filtrar armaduras por tipo
     */
    public List<ArmorDTO> getArmorsByType(String type) {
        // ✅ PADRÃO: Validar NULL + vazio
        if (type == null || type.trim().isEmpty()) {
            log.warn("❌ Tipo de armadura inválido: {}", type);
            throw new InvalidOperationException("Tipo de armadura não pode ser vazio");
        }

        log.debug("🔍 Buscando armaduras com tipo: {}", type);

        List<Armor> armors = armorRepository.findAll().stream().filter(a -> a.getArmorType().name().equals(type)).toList();
        if (armors.isEmpty()) {
            log.info("⚠️  Nenhuma armadura encontrada para tipo: {}", type);
        }

        return armors.stream().map(this::convertToDTO).toList();
    }
    
    /**
     * Filtrar armaduras por raridade
     */
    public List<ArmorDTO> getArmorsByRarity(String rarity) {
        log.debug("Buscando armaduras com raridade: {}", rarity);
        if (rarity == null || rarity.trim().isEmpty()) {
            throw new InvalidOperationException("Raridade não pode ser vazia");
        }
        return armorRepository.findAll().stream().filter(a -> a.getRarity().name().equals(rarity)).toList()
            .stream()
            .map(this::convertToDTO)
            .toList();
    }
    
    /**
     * Validar se personagem pode equipar armadura
     */
    @Transactional(readOnly = true)
    public boolean canEquipArmor(ArmorDTO armor, Integer level, Integer con) {

        log.debug("Validando requisitos da armadura: {}", armor != null ? armor.getName() : "null");

        // ✅ Validar se armor é null
        if (armor == null) {
            log.error("ArmorDTO não pode ser nulo");
            throw new InvalidOperationException("Armadura inválida");
        }

        // ✅ Validar se requisitos são nulos
        if (armor.getMinLevel() == null || armor.getMinCon() == null) {
            log.error("Requisitos da armadura incompletos: minLevel={}, minCon={}",
                    armor.getMinLevel(), armor.getMinCon());
            throw new InvalidOperationException("Requisitos da armadura estão incompletos");
        }

        // ✅ Validar se atributos do ‘player’ são nulos
        if (level == null || con == null) {
            log.error("Atributos do player inválidos: level={}, con={}", level, con);
            throw new InvalidOperationException("Atributos do personagem inválidos");
        }

        // ✅ Agora é seguro comparar
        boolean canEquip = level >= armor.getMinLevel() && con >= armor.getMinCon();

        log.debug("Resultado validação: {} (level={} >= minLevel={}) && (con={} >= minCon={})",
                canEquip, level, armor.getMinLevel(), con, armor.getMinCon());
        return canEquip;
    }
    
    /**
     * Converter Entity para DTO
     */
    public ArmorDTO convertToDTO(Armor armor) {
        // ✅ PADRÃO
        if (armor == null) {
            log.warn("❌ Tentativa de converter Armor null");
            return null;
        }
        
        // Calcular preço de venda (50%)
        Long sellPrice = armor.getTpCost() != null 
            ? (armor.getTpCost() * 50) / 100 
            : 0L;
        
        // Display name formatado
        String displayName = buildDisplayName(armor.getName(), armor.getArmorType(), armor.getRarity());
        
        // Display type
        String armorTypeDisplay = getArmorTypeDisplay(armor.getArmorType().name());
        
        return ArmorDTO.builder()
            .id(armor.getId())
            .name(armor.getName())
            .description(armor.getDescription())
            .imageUrl(armor.getImageUrl())
            .armorType(armor.getArmorType().name())
            .armorTypeIcon(getArmorTypeIcon(armor.getArmorType().name()))
            .rarity(armor.getRarity().name())
            .rarityIcon(getRarityIcon(armor.getRarity().name()))
            .rarityColor(getRarityColor(armor.getRarity().name()))
            .tpCost(armor.getTpCost())
            .sellPrice(sellPrice)
            .strBonus(armor.getStrBonus())
            .conBonus(armor.getConBonus())
            .dexBonus(armor.getDexBonus())
            .minLevel(armor.getMinLevel())
            .minCon(armor.getMinCon())
            .requirementsDescription(buildRequirementsDescription(armor))
            .isActive(armor.getIsActive())
            .displayName(displayName)
            .bonusDescription(buildBonusDescription(armor))
            .build();
    }
    
    /**
     * Construir descrição formatada dos requisitos
     */
    private String buildRequirementsDescription(Armor armor) {
        StringBuilder sb = new StringBuilder();
        
        if (armor.getMinLevel() != null && armor.getMinLevel() > 0) {
            sb.append("Nível ").append(armor.getMinLevel()).append(" | ");
        }
        
        if (armor.getMinCon() != null && armor.getMinCon() > 0) {
            sb.append("CON ").append(armor.getMinCon()).append(" | ");
        }
        
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 3); // Remover " | " final
        }
        
        return sb.toString().isEmpty() ? "Sem requisitos" : sb.toString();
    }
    
    /**
     * Construir descrição dos bônus
     */
    private String buildBonusDescription(Armor armor) {
        StringBuilder sb = new StringBuilder();
        
        if (armor.getStrBonus() != null && armor.getStrBonus() > 0) {
            sb.append("+").append(armor.getStrBonus()).append(" STR ");
        }
        if (armor.getConBonus() != null && armor.getConBonus() > 0) {
            sb.append("+").append(armor.getConBonus()).append(" CON ");
        }
        if (armor.getDexBonus() != null && armor.getDexBonus() > 0) {
            sb.append("+").append(armor.getDexBonus()).append(" DEX ");
        }
        
        return sb.toString().trim().isEmpty() ? "Sem bônus" : sb.toString().trim();
    }
    
    /**
     * Construir display name formatado
     */
   private String buildDisplayName(String name, ArmorType type, ArmorRarity rarity) {
        String rarityIcon = getRarityIcon(rarity.name());
        String typeIcon = getArmorTypeIcon(type.name());
        return rarityIcon + " " + name + " " + typeIcon;
    }
    
    /**
     * Obter display do tipo de armadura
     */
    private String getArmorTypeDisplay(String type) {
        return switch (type) {
            case "LEVE" -> "Leve";
            case "NORMAL" -> "Normal";
            case "PESADA" -> "Pesada";
            default -> type;
        };
    }
    
    /**
     * Obter ícone do tipo de armadura
     */
    private String getArmorTypeIcon(String type) {
        return switch (type) {
            case "LEVE" -> "🧵";
            case "NORMAL" -> "🛡️";
            case "PESADA" -> "⚔️";
            default -> "•";
        };
    }
    
    /**
     * Obter ícone da raridade
     */
    private String getRarityIcon(String rarity) {
        return switch (rarity) {
            case "LENDARIO" -> "🌟";
            case "EPICO" -> "⭐";
            case "RARO" -> "✨";
            case "INCOMUM" -> "💫";
            default -> "•";
        };
    }
    
    /**
     * Obter cor hex da raridade
     */
    private String getRarityColor(String rarity) {
        return switch (rarity) {
            case "LENDARIO" -> "#FFD700";
            case "EPICO" -> "#9B59B6";
            case "RARO" -> "#3498DB";
            case "INCOMUM" -> "#95A5A6";
            default -> "#ECF0F1";
        };
    }
}
