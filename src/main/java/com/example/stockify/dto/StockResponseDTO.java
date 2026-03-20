package com.example.stockify.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockResponseDTO {
    private StockMetaDTO meta;
    private List<StockCandleDTO> chart;
}

