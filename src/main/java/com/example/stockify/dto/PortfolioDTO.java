package com.example.stockify.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {

    private Integer id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Stock name is required")
    private String stockName;

    @NotNull(message = "Average price is required")
    @PositiveOrZero(message = "Average price cannot be negative")
    private Float averagePrice;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    @PositiveOrZero(message = "Investment cannot be negative")
    private Float investment;
}