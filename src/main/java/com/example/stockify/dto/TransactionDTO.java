package com.example.stockify.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Integer id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Stock name is required")
    private String stockName;

    @NotBlank(message = "Transaction type is required")
    private String type; // BUY / SELL

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Float price;

    @PositiveOrZero(message = "Amount cannot be negative")
    private Float amount;

    private LocalDateTime txnTime;

    private String tradeType; // DELIVERY / INTRADAY
}