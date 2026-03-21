package com.example.stockify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockCandleDTO {

    private Long time;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;
}