package com.mugen.backend.enums;

import lombok.Getter;

/**
 * Raridade da arma/armadura/material
 * Define a qualidade e poder do item
 * Cada raridade tem multiplicador e cor associada
 */
@Getter
public enum WeaponRarity {
    LENDARIO("Lendário", 1.5f, "#FFD700", "SS"),      // Dourado
    EPICO("Épico", 1.3f, "#9C27B0", "S"),            // Roxo
    RARO("Raro", 1.15f, "#2196F3", "R"),             // Azul
    INCOMUM("Incomum", 1.0f, "#4CAF50", "I"),        // Verde
    COMUM("Comum", 0.9f, "#FFFFFF", "C"),            // Branco
    POBRE("Pobre", 0.8f, "#9E9E9E", "P"),           // Cinza
    QUEBRADO("Quebrado", 0.7f, "#795548", "Q"),      // Marrom
    DESTRUIDO("Destruído", 0.6f, "#000000", "D");    // Preto

    private final String displayName;
    private final float multiplier;
    private final String colorHex;
    private final String icon;

    WeaponRarity(String displayName, float multiplier, String colorHex, String icon) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.colorHex = colorHex;
        this.icon = icon;
    }

    /**
     * Retorna a raridade com ícone e nome
     * Ex: "🌟 Lendário"
     */
    public String getDisplayWithIcon() {
        return icon + " " + displayName;
    }
}
