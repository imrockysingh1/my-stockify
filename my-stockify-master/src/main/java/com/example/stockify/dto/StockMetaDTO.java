package com.example.stockify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockMetaDTO {

    private String symbol;
    private String name;
    private String currency;
    private String exchange;

    private Double price;
    private Double previousClose;
    private Double change;
    private Double changePercent;

    private Double dayHigh;
    private Double dayLow;
    private Double week52High;
    private Double week52Low;
    private Long volume;
}