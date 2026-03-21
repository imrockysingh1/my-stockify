package com.example.stockify.dto;

import lombok.Data;

@Data
public class BuyOrderRequestDTO {

    private String symbol;
    private int quantity;
    private String orderType; // MARKET / LIMIT
    private Double price; // optional
}