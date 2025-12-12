package com.mugen.backend.enums;


import lombok.Getter;

/**
 * Tipo elemental da arma
 * Elementos do sistema de combate
 */

@Getter
public enum ElementalType {
    FIRE("Fogo", "🔥", "Elemento do fogo ardente"),
    ICE("Gelo", "❄️", "Elemento do gelo congelante"),
    WIND("Vento", "💨", "Elemento do vento veloz"),
    EARTH("Terra", "🌍", "Elemento da terra sólida"),
    WATER("Água", "💧", "Elemento da água fluente"),
    DARKNESS("Escuridão", "🌑", "Elemento das trevas"),
    LIGHT("Luz", "✨", "Elemento da luz sagrada"),
    NEUTRAL("Neutro", "⚪", "Algo totalmente normal e equilibrado"),
    ELECTRIC("Elétrico", "⚡", "Elemento da energia e eletricidade pura"),
    NATURE("Natureza", "🌿", "Elemento da vida, plantas e crescimento"),
    POISON("Veneno", "☠️", "Elemento das toxinas e da corrupção"),
    METAL("Metal", "⛓️", "Elemento da força e resistência metálica"),
    SPIRIT("Espírito", "👻", "Elemento etéreo das almas e entidades místicas"),
    SAND("Areia", "🏜️", "Elemento das dunas e tempestades do deserto"),
    LAVA("Lava", "🌋", "Elemento do magma em chamas e destruição vulcânica"),
    STEAM("Vapor", "♨️", "Elemento da fusão entre fogo e água"),
    CRYSTAL("Cristal", "🔮", "Elemento da pureza e estrutura mágica sólida"),
    SOUND("Som", "🎵", "Elemento das vibrações e ondas sonoras"),
    CHAOS("Caos", "🌀", "Elemento da instabilidade e poder imprevisível"),
    ORDER("Ordem", "⚖️", "Elemento do equilíbrio e da harmonia universal");


    private final String displayName;
    private final String icon;
    private final String description;


    ElementalType(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    /**
     * Retorna o elemento com ícone
     * Ex: "🔥 Fogo"
     */
    public String getDisplayWithIcon() {
        return icon + " " + displayName;
    }
}
