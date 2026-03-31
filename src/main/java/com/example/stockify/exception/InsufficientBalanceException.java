package com.example.stockify.exception;

import io.jsonwebtoken.RequiredTypeException;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
