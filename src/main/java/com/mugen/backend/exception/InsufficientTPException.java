package com.mugen.backend.exception;

public class InsufficientTPException extends RuntimeException {

    public InsufficientTPException(String message) {
        super(message);
    }

    public InsufficientTPException(Integer required, Integer available) {
        super(String.format("Insufficient TP. Required: %d, Available: %d", required, available));
    }
}
