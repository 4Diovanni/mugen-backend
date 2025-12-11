package com.mugen.backend.enums;

import lombok.Getter;

/**
 * Status do equipamento
 * Simplificado para viés narrativo do RPG
 * Ao invés de slots complexos, apenas: está ou não está usando
 */
@Getter
public enum EquipmentStatus {
    NAO_EQUIPADO("Não Equipado", "Nenhum item equipado"),
    EQUIPADO_ARMA("Equipado (Arma)", "Apenas arma equipada"),
    EQUIPADO_ARMADURA("Equipado (Armadura)", "Apenas armadura equipada"),
    EQUIPADO_AA("Equipado (Arma + Armadura)", "Arma e armadura equipadas");

    private final String displayName;
    private final String description;

    EquipmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Verifica se está completamente equipado (arma + armadura)
     */
    public boolean isFullyEquipped() {
        return this == EQUIPADO_AA;
    }

    /**
     * Verifica se tem algo equipado
     */
    public boolean hasEquipment() {
        return this != NAO_EQUIPADO;
    }

    /**
     * Verifica se tem arma equipada
     */
    public boolean hasWeapon() {
        return this == EQUIPADO_ARMA || this == EQUIPADO_AA;
    }

    /**
     * Verifica se tem armadura equipada
     */
    public boolean hasArmor() {
        return this == EQUIPADO_ARMADURA || this == EQUIPADO_AA;
    }
}
