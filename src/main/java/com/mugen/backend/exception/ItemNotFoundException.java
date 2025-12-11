package com.mugen.backend.exception;

/**
 * Exceção para item não encontrado no inventário
 */
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String itemType, Integer itemId) {
        super(String.format("%s com ID %d não encontrado no inventário", itemType, itemId));
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
