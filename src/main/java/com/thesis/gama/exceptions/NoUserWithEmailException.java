package com.thesis.gama.exceptions;

public class NoUserWithEmailException extends Exception {

    public NoUserWithEmailException(String message) {
        super(message);
    }
}