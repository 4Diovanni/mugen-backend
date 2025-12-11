package com.mugen.backend.exception;

/**
 * Exceção para equipamento inválido ou operação de equipamento
 */
public class EquipmentException extends RuntimeException {
    public EquipmentException(String message) {
        super(message);
    }

    public EquipmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
