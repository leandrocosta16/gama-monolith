package com.thesis.gama.exceptions;

public class ExistingForeignKeysException extends Exception {
    public ExistingForeignKeysException(String message) {
        super(message);
    }
}
