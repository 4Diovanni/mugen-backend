package com.mugen.backend.exception;

import java.util.UUID;

public class SkillNotFoundException extends RuntimeException {

    public SkillNotFoundException(Integer id) {
        super("Skill not found with id: " + id);
    }

    public SkillNotFoundException(String message) {
        super(message);
    }
}
