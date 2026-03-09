package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.PortfolioRepository;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.services.JwtService;
import com.example.stockify.services.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import javax.management.relation.RelationNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final JsonMapper.Builder builder;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final JwtService jwtService;

    public PortfolioController(PortfolioService portfolioService, JsonMapper.Builder builder, UserRepository userRepository, PortfolioRepository portfolioRepository, JwtService jwtService) {
        this.portfolioService = portfolioService;
        this.builder = builder;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.jwtService = jwtService;
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<ApiResponse<List<PortfolioDTO>>> getPortfolio(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {

        String token = authHeader.substring(7);
        String loggedInUser = jwtService.extractUsername(token);

        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("You are not authorized to access this profile");
        }

        {
            userRepository.findById(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found " + username));

            List<PortfolioDTO> data = portfolioService.getUserPortfolio(username);

            ApiResponse<List<PortfolioDTO>> response =
                    ApiResponse.<List<PortfolioDTO>>builder()
                            .success(true)
                            .data(data)
                            .build();
            return ResponseEntity.ok(response);
        }
    }
}
