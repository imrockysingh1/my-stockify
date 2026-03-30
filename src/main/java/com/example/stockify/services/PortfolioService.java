package com.example.stockify.services;

import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.dto.PortfolioSummaryDTO;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.entities.PortfolioEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.PortfolioRepository;
import com.example.stockify.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final StockService stockService;

    public PortfolioService(PortfolioRepository portfolioRepository,
                            ModelMapper modelMapper,
                            UserRepository userRepository,
                            StockService stockService) {
        this.portfolioRepository = portfolioRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.stockService = stockService;
    }

    @Transactional(readOnly = true)
    public PortfolioSummaryDTO getUserPortfolio(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }

        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<PortfolioDTO> stocks = portfolioRepository.findByUserUsername(username);

        Map<String, StockResponseDTO> stockCache = new HashMap<>();

        List<PortfolioDTO> stocksFetched = stocks
                                        .stream()
                                        .map(dto-> {

            float totalInvestment = dto.getInvestment();
            int quantity = dto.getQuantity();

            StockResponseDTO stock = stockCache.computeIfAbsent(
                    dto.getStockName(),
                    s -> stockService.getStock(s, "1d", "1m")
            );

            double currentPrice = stock.getMeta().getPrice();
            double prevClose = stock.getMeta().getPreviousClose();

            double currentValue = currentPrice * quantity;
            double profitLoss = currentValue - totalInvestment;
            double profitLossPercent = (profitLoss / totalInvestment) * 100;

            double oneDayReturn = (currentPrice - prevClose) * quantity;
            double oneDayReturnPercent = ((currentPrice - prevClose) / prevClose) * 100;

            dto.setInvestment(totalInvestment);
            dto.setCurrentPrice(currentPrice);
            dto.setCurrentValue(currentValue);
            dto.setProfitLoss(profitLoss);
            dto.setProfitLossPercent(profitLossPercent);
            dto.setOneDayReturn(oneDayReturn);
            dto.setOneDayReturnPercent(oneDayReturnPercent);

            return dto;

        }).toList();
//        String username = stocks.stream().mapToDouble(PortfolioDTO::getUser).sum();
        double totalInvestment = stocksFetched.stream().mapToDouble(PortfolioDTO::getInvestment).sum();
        double totalCurrentValue = stocksFetched.stream().mapToDouble(PortfolioDTO::getCurrentValue).sum();
        double totalProfitLoss = totalCurrentValue - totalInvestment;
        double totalProfitLossPercent = (totalProfitLoss / totalInvestment) * 100;
        double totalOneDayReturn = stocksFetched.stream().mapToDouble(PortfolioDTO::getOneDayReturn).sum();
        double totalOneDayReturnPercent = (totalOneDayReturn / totalInvestment) * 100;

        return new PortfolioSummaryDTO(
                totalInvestment,
                totalCurrentValue,
                totalProfitLoss,
                totalProfitLossPercent,
                totalOneDayReturn,
                totalOneDayReturnPercent,
                stocksFetched
        );
    }
}