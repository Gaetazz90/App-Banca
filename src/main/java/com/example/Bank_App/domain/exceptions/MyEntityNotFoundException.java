package com.example.Bank_App.domain.exceptions;

public class MyEntityNotFoundException extends RuntimeException {
    public MyEntityNotFoundException(String message) {
        super(message);
    }
}
