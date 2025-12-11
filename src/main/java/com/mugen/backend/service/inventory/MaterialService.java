package com.mugen.backend.service.inventory;


import com.mugen.backend.dto.inventory.ArmorDTO;
import com.mugen.backend.dto.inventory.MaterialDTO;

import com.mugen.backend.entity.inventory.Material;
import com.mugen.backend.enums.ArmorRarity;
import com.mugen.backend.enums.ArmorType;
import com.mugen.backend.repository.inventory.MaterialRepository;
import com.mugen.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    /**
     * Obter material por ID
     */
    public MaterialDTO getMaterialById(Integer materialId) {
        log.debug("Buscando material com ID: {}", materialId);
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Material não encontrado com ID: " + materialId
                ));
        return convertToDTO(material);
    }

//    /**
//     * Listar todos os materiais
//     */
//    public List<MaterialDTO> getAllMaterials() {
//        log.debug("Listando todos os materiais");
//        return materialRepository.findAll()
//                .stream()
//                .map(this::convertToDTO)
//                .toList();
//    }

    /**
     * Listar todas as materiais ativos
     */
    public Page<MaterialDTO> getActiveMaterials(Pageable pageable) {
        log.debug("Listando materiais ativos");
        Objects.requireNonNull(pageable, "Pageable não pode ser nulo");
        return materialRepository.findAll(pageable)
                .map(this::convertToDTO);
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
    /**
     * Construir display name formatado
     */
    private String buildDisplayName(String name, String rarity) {
        // ✅ NULL-SAFE
        String rarityIcon = getRarityIcon(rarity != null ? rarity : "");
        return rarityIcon + " " + (name != null ? name : "Desconhecida");
    }

    /**
     * Converter Entity para DTO
     */
    public MaterialDTO convertToDTO(Material material) {
        if (material == null) {
            log.warn("❌ Tentativa de converter Material null para DTO");
            return null;
        }
        // Display name formatado
        String displayName = buildDisplayName(material.getName(), material.getRarity().name());


        return MaterialDTO.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .imageUrl(material.getImageUrl())
                .rarity(material.getRarity().name())
                .rarityIcon(getRarityIcon(material.getRarity().name()))
                .rarityColor(getRarityColor(material.getRarity().name()))
                .displayName(displayName)
                .build();
    }

}
