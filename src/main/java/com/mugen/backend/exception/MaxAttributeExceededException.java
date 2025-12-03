package com.mugen.backend.exception;

public class MaxAttributeExceededException extends RuntimeException {

    public MaxAttributeExceededException(String message) {
        super(message);
    }

    public MaxAttributeExceededException(String attributeName, Integer maxValue) {
        super(String.format("Attribute %s cannot exceed %d", attributeName, maxValue));
    }
}
