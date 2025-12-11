package com.mugen.backend.exception;

/**
 * Exceção para ouro insuficiente
 */
public class InsufficientGoldException extends RuntimeException {
    public InsufficientGoldException(long needed, long available) {
        super(String.format("Ouro insuficiente. Necessário: %d, Disponível: %d", needed, available));
    }

    public InsufficientGoldException(String message) {
        super(message);
    }
}
