package com.thesis.gama.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class NoDataFoundException extends Exception {

    public NoDataFoundException(String message) {
        super(message);
    }
}