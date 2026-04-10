package com.example.stockify.dto;

import com.example.stockify.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class BuyOrderRequestDTO {

    private String symbol;
    private int quantity;
    private String orderType; // MARKET / LIMIT
    private Double price;
    private String status;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;
}