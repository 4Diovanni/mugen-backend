package com.mugen.backend.controller;

import com.mugen.backend.dto.CreateSkillDTO;
import com.mugen.backend.dto.UpdateSkillDTO;
import com.mugen.backend.entity.Skill;
import com.mugen.backend.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {

    private final SkillService skillService;

    /**
     * POST /skills
     * Criar nova skill
     */
    @PostMapping
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody CreateSkillDTO dto) {
        log.info("Request to create skill: {}", dto.getName());
        Skill created = skillService.createSkill(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /skills
     * Listar todas as skills
     */
    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        log.info("Request to get all skills");
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /skills/active
     * Listar apenas skills ativas
     */
    @GetMapping("/active")
    public ResponseEntity<List<Skill>> getActiveSkills() {
        log.info("Request to get active skills");
        List<Skill> skills = skillService.getActiveSkills();
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /skills/{id}
     * Buscar skill por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Integer id) {
        log.info("Request to get skill by id: {}", id);
        Skill skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }

    /**
     * GET /skills/type/{type}
     * Buscar skills por tipo
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Skill>> getSkillsByType(@PathVariable String type) {
        log.info("Request to get skills by type: {}", type);
        List<Skill> skills = skillService.getSkillsByType(type);
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /skills/search?name={name}
     * Buscar skills por nome (parcial)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Skill>> searchSkillsByName(@RequestParam String name) {
        log.info("Request to search skills by name: {}", name);
        List<Skill> skills = skillService.getSkillsByName(name);
        return ResponseEntity.ok(skills);
    }

    /**
     * GET /skills/ordered-by-cost
     * Listar skills ordenadas por custo
     */
    @GetMapping("/ordered-by-cost")
    public ResponseEntity<List<Skill>> getSkillsOrderedByCost() {
        log.info("Request to get skills ordered by cost");
        List<Skill> skills = skillService.getSkillsOrderedByCost();
        return ResponseEntity.ok(skills);
    }

    /**
     * PUT /skills/{id}
     * Atualizar skill completa
     */
    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSkillDTO dto) {
        log.info("Request to update skill: {}", id);
        Skill updated = skillService.updateSkill(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /skills/{id}/deactivate
     * Desativar skill (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Skill> deactivateSkill(@PathVariable Integer id) {
        log.info("Request to deactivate skill: {}", id);
        skillService.deactivateSkill(id);
        Skill skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }

    /**
     * DELETE /skills/{id}
     * Deletar skill permanentemente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        log.info("Request to delete skill: {}", id);
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
