package com.example.Bank_App.domain.exceptions;

public class IllegalTransactionException extends RuntimeException {
  public IllegalTransactionException(String message) {
    super(message);
  }
}
