package com.example.stockify.exception;

public class StockDataNotFoundException extends RuntimeException {
    public StockDataNotFoundException(String message) {
        super(message);
    }
}
