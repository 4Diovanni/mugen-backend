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
public class CharacterTransformationId implements Serializable {

    @Column(name = "character_id")
    private UUID characterId;

    @Column(name = "transformation_id")
    private Integer transformationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterTransformationId that = (CharacterTransformationId) o;

        if (characterId != null ? !characterId.equals(that.characterId) : that.characterId != null) return false;
        return transformationId != null ? transformationId.equals(that.transformationId) : that.transformationId == null;
    }

    @Override
    public int hashCode() {
        int result = characterId != null ? characterId.hashCode() : 0;
        result = 31 * result + (transformationId != null ? transformationId.hashCode() : 0);
        return result;
    }
}
