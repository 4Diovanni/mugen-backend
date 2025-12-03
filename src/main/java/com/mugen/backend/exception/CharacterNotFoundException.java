package com.mugen.backend.exception;

import java.util.UUID;

public class CharacterNotFoundException extends RuntimeException {

    public CharacterNotFoundException(UUID id) {
        super("Character not found with id: " + id);
    }

    public CharacterNotFoundException(String message) {
        super(message);
    }
}
