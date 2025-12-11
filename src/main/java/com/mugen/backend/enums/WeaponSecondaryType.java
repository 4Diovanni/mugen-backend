package com.mugen.backend.enums;

import lombok.Getter;

/**
 * Tipo secundário da arma - Define a categoria específica

 * Armas de combate corpo-a-corpo e à distância
 */
@Getter
public enum WeaponSecondaryType {
    // Armas de combate
    ESPADA("Espada", "⚔️", "Arma versátil de lâmina"),
    ARCO("Arco", "🏹", "Arma de longo alcance"),
    MAGIA("Magia", "✨", "Canalização de poder arcano"),
    MACHADO("Machado", "🪓", "Arma pesada de impacto"),
    LANCA("Lança", "🔱", "Arma de alcance médio perfurante"),
    MARTELO("Martelo", "🔨", "Arma de impacto devastador"),
    ADAGA("Adaga", "🗡️", "Arma leve e rápida"),
    CETRO("Cetro", "🪄", "Instrumento de canalização mágica");

    private final String displayName;
    private final String icon;
    private final String description;

    WeaponSecondaryType(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

}
