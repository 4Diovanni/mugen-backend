package com.mugen.backend.enums;

import lombok.Getter;

/**
 * Tipo de armadura
 * 
 * LEVE: Maior mobilidade, menor proteção
 * NORMAL: Balanceada
 * PESADA: Maior proteção, menor mobilidade
 */
@Getter
public enum ArmorType {
    LEVE("Leve", "🏃", "Armadura leve - Maior mobilidade, menor proteção"),
    NORMAL("Normal", "🛡️", "Armadura normal - Balanceada entre proteção e mobilidade"),
    PESADA("Pesada", "🏰", "Armadura pesada - Maior proteção, menor mobilidade");

    private final String displayName;
    private final String icon;
    private final String description;

    ArmorType(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    /**
     * Retorna o tipo com ícone
     * Ex: "🛡️ Normal"
     */
    public String getDisplayWithIcon() {
        return icon + " " + displayName;
    }
}
