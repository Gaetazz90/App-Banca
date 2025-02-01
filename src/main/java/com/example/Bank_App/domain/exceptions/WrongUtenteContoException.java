package com.example.Bank_App.domain.exceptions;

public class WrongUtenteContoException extends RuntimeException {
    public WrongUtenteContoException(String message) {
        super(message);
    }
}
