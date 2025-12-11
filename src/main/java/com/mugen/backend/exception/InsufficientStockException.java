package com.mugen.backend.exception;

/**
 * Exceção para estoque insuficiente
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int requested, int available) {
        super(String.format("Estoque insuficiente. Solicitado: %d, Disponível: %d", requested, available));
    }

    public InsufficientStockException(String itemType, int requested, int available) {
        super(String.format("%s: Estoque insuficiente. Solicitado: %d, Disponível: %d", itemType, requested, available));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
