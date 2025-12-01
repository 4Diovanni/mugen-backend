package com.mugen.backend.repository;

import com.mugen.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    List<Skill> findByIsActiveTrue();

    List<Skill> findBySkillType(Skill.SkillType skillType);

    List<Skill> findBySkillTypeAndIsActiveTrue(Skill.SkillType skillType);
}
