package com.example.stockify.services;

import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.repositories.PortfolioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ModelMapper modelMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, ModelMapper modelMapper) {
        this.portfolioRepository = portfolioRepository;
        this.modelMapper = modelMapper;
    }

    public List<PortfolioDTO> getUserPortfolio(String username) {
        return portfolioRepository.findByUserUsername(username)
                .stream()
                .map(entity -> modelMapper.map(entity , PortfolioDTO.class))
                .toList();
    }
}
