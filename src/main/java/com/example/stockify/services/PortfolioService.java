package com.example.stockify.services;

import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.PortfolioRepository;
import com.example.stockify.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getUserPortfolio(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");   //need to build
        }
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return portfolioRepository.findByUserUsername(username)
                .stream()
                .map(entity -> {
                    PortfolioDTO dto = modelMapper.map(entity, PortfolioDTO.class);
                    dto.setUsername(entity.getUser().getUsername());
                    return dto;
                })
                .toList();
    }
}
