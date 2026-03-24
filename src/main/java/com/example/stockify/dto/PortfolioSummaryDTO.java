package com.example.stockify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDTO {

    private double totalInvestment;
    private double totalCurrentValue;
    private double totalProfitLoss;
    private double totalProfitLossPercent;

    private double totalOneDayReturn;
    private double totalOneDayReturnPercent;

    private List<PortfolioDTO> stocks;
}