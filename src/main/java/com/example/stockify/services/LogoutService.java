package com.example.stockify.services;

import com.example.stockify.entities.AuthTokenEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.AuthTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final AuthTokenRepository authTokenRepository;

    public LogoutService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public void logout(String token , String username){
        AuthTokenEntity authToken = authTokenRepository.findByToken(token)
                .orElseThrow(()->new ResourceNotFoundException("Token not found"));

        authToken.setExpired(true);
        authTokenRepository.save(authToken);
    }

}
