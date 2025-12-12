package com.mugen.backend.exception;

/**
 * Exceção para requisitos de item não atendidos
 * Ex: Nível insuficiente, atributos insuficientes
 */
public class ItemRequirementsNotMetException extends RuntimeException {
    public ItemRequirementsNotMetException(String requirement) {
        super(String.format("Requisitos não atendidos: %s", requirement));
    }

    public ItemRequirementsNotMetException(String itemName, String requirement, int required, int current) {
        super(String.format("%s: %s insuficiente. Necessário: %d, Seu: %d", itemName, requirement, required, current));
    }

}
