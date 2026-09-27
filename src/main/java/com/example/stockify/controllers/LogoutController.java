package com.example.stockify.controllers;

import com.example.stockify.dto.LogoutResponseDTO;
import com.example.stockify.entities.AuthTokenEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.AuthTokenRepository;
import com.example.stockify.services.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/logout/{username}")
public class LogoutController {

    private final LogoutService logoutService;
    private final AuthTokenRepository authTokenRepository;

    public LogoutController(LogoutService logoutService, AuthTokenRepository authTokenRepository) {
        this.logoutService = logoutService;
        this.authTokenRepository = authTokenRepository;
    }

    @PostMapping
    public ResponseEntity<String> logout(
            @PathVariable String username,
            @RequestHeader("Authorization") String authToken) {

        if(authToken == null || !authToken.startsWith("Bearer")){
            return ResponseEntity
                    .badRequest()
                    .body("Authorization token is required");
        }
        LogoutResponseDTO user = authTokenRepository.findLogoutDetailsByToken(authToken.substring(7))
                        .orElseThrow(()-> new ResourceNotFoundException("Token not found"));

        if(!user.getUsername().equals(username)){
            ResponseEntity
                    .badRequest()
                    .body("Unauthorized");
        }
        logoutService.logout(authToken.substring(7),username);
        return ResponseEntity.ok(
                "Logout successful"
        );
    }

}
