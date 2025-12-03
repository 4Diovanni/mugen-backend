package com.mugen.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSkillId implements Serializable {

    @Column(name = "character_id")
    private UUID characterId;

    @Column(name = "skill_id")
    private Integer skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterSkillId that = (CharacterSkillId) o;

        if (characterId != null ? !characterId.equals(that.characterId) : that.characterId != null) return false;
        return skillId != null ? skillId.equals(that.skillId) : that.skillId == null;
    }

    @Override
    public int hashCode() {
        int result = characterId != null ? characterId.hashCode() : 0;
        result = 31 * result + (skillId != null ? skillId.hashCode() : 0);
        return result;
    }
}
