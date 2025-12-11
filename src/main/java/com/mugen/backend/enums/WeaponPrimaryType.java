package com.mugen.backend.enums;

import lombok.Getter;

/**
 * Tipo primário da arma - Define a função principal
 * 
 * ATAQUE: Focado em causar dano
 * DEFESA: Focado em proteção
 * UTILIDADE: Funções especiais e suporte
 * SUPORTE: Buffs e auxílio ao time
 */
@Getter
public enum WeaponPrimaryType {
    ATAQUE("Ataque", "Focado em causar dano"),
    DEFESA("Defesa", "Focado em proteção"),
    UTILIDADE("Utilidade", "Funções especiais e suporte"),
    SUPORTE("Suporte", "Buffs e auxílio ao time");

    private final String displayName;
    private final String description;

    WeaponPrimaryType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}
