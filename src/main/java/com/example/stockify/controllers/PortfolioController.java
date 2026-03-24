package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.dto.PortfolioSummaryDTO;
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
    public ResponseEntity<ApiResponse<PortfolioSummaryDTO>> getPortfolio(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {

        try {
            if (authHeader == null || !authHeader.startsWith("Brearer ")) {
                throw new AccessDeniedException("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String loggedInUser = jwtService.extractUsername(token);
            System.out.println("Logged In user " + loggedInUser);
            System.out.println("username" + username);

            if (!loggedInUser.equals(username)) {
                throw new AccessDeniedException("You are not authorized to access this profile");
            }
        }catch (Exception e){
            throw new AccessDeniedException("Invalid or expired token");
        }

        {
            userRepository.findById(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found " + username));

            PortfolioSummaryDTO data = portfolioService.getUserPortfolio(username);

            ApiResponse<PortfolioSummaryDTO> response =
                    ApiResponse.<PortfolioSummaryDTO>builder()
                            .success(true)
                            .data(data)
                            .build();
            return ResponseEntity.ok(response);
        }
    }
}
