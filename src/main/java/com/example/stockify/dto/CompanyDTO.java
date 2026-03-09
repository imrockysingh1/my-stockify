package com.example.stockify.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    @NotBlank(message = "NSE code is required")
    private String nseCode;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Stock price is required")
    @Positive(message = "Stock price must be positive")
    private Float stockPrice;

    @PositiveOrZero(message = "Year low cannot be negative")
    private Integer yearLow;

    @PositiveOrZero(message = "Year high cannot be negative")
    private Integer yearHigh;

    private String description;

    private String bseCode;
}