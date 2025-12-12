package com.mugen.backend.service;

import com.mugen.backend.dto.skill.CreateSkillDTO;
import com.mugen.backend.dto.skill.UpdateSkillDTO;
import com.mugen.backend.entity.Skill;
import com.mugen.backend.entity.Skill.SkillType;
import com.mugen.backend.exception.SkillNotFoundException;
import com.mugen.backend.repository.skills.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;

    // ========================================
    // CRIAR SKILL
    // ========================================

    @Transactional
    public Skill createSkill(CreateSkillDTO dto) {
        log.info("Creating new skill: {}", dto.getName());

        // Validar nome único
        if (skillRepository.existsByName(dto.getName())) {
            throw new IllegalStateException("Skill with name '" + dto.getName() + "' already exists");
        }

        Skill skill = Skill.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .skillType(SkillType.valueOf(dto.getSkillType())) // Converter String para Enum
                .baseTpCost(dto.getBaseTpCost() != null ? dto.getBaseTpCost() : 10)
                .maxLevel(dto.getMaxLevel() != null ? dto.getMaxLevel() : 10)
                .requiredLevel(dto.getRequiredLevel() != null ? dto.getRequiredLevel() : 1)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        Skill saved = skillRepository.save(skill);
        log.info("Skill created successfully with id: {}", saved.getId());

        return saved;
    }

    // ========================================
    // BUSCAR SKILLS
    // ========================================

    @Transactional(readOnly = true)
    public List<Skill> getAllSkills() {
        log.info("Finding all skills");
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Skill> getActiveSkills() {
        log.info("Finding active skills");
        return skillRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(Integer id) {
        log.info("Finding skill by id: {}", id);
        return skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByType(String type) {
        log.info("Finding skills by type: {}", type);
        return skillRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsByName(String name) {
        log.info("Searching skills by name: {}", name);
        return skillRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsOrderedByCost() {
        log.info("Finding skills ordered by cost");
        return skillRepository.findAllByOrderByBaseTpCostAsc();
    }

    // ========================================
    // ATUALIZAR SKILL
    // ========================================

    @Transactional
    public Skill updateSkill(Integer id, UpdateSkillDTO dto) {
        log.info("Updating skill: {}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));

        // Validar nome único se estiver a mudar
        if (dto.getName() != null && !dto.getName().equals(skill.getName())) {
            if (skillRepository.existsByName(dto.getName())) {
                throw new IllegalStateException("Skill with name '" + dto.getName() + "' already exists");
            }
            skill.setName(dto.getName());
        }

        // Atualizar campos opcionais
        if (dto.getDescription() != null) {
            skill.setDescription(dto.getDescription());
        }
        if (dto.getSkillType() != null) {
            skill.setSkillType(SkillType.valueOf(dto.getSkillType())); // Converter String para Enum
        }
        if (dto.getBaseTpCost() != null) {
            skill.setBaseTpCost(dto.getBaseTpCost());
        }
        if (dto.getMaxLevel() != null) {
            skill.setMaxLevel(dto.getMaxLevel());
        }
        if (dto.getRequiredLevel() != null) {
            skill.setRequiredLevel(dto.getRequiredLevel());
        }
        if (dto.getIsActive() != null) {
            skill.setIsActive(dto.getIsActive());
        }

        Skill updated = skillRepository.save(skill);
        log.info("Skill updated successfully: {}", updated.getName());

        return updated;
    }

    // ========================================
    // DELETAR SKILL
    // ========================================

    @Transactional
    public void deleteSkill(Integer id) {
        log.info("Deleting skill: {}", id);

        if (!skillRepository.existsById(id)) {
            throw new SkillNotFoundException(id);
        }

        skillRepository.deleteById(id);
        log.info("Skill deleted successfully: {}", id);
    }

    @Transactional
    public void deactivateSkill(Integer id) {
        log.info("Deactivating skill: {}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));

        skill.setIsActive(false);
        skillRepository.save(skill);

        log.info("Skill deactivated successfully: {}", id);
    }
}
