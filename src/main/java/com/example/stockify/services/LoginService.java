package com.example.stockify.services;

import com.example.stockify.dto.LoginRequestDTO;
import com.example.stockify.entities.AuthTokenEntity;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.AuthTokenRepository;
import com.example.stockify.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthTokenRepository authTokenRepository;

    public LoginService(UserRepository userRepository, JwtService jwtService, AuthTokenRepository authTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public String login(@Valid LoginRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid Username"));

        if(!user.getPassword().equals(request.getPassword())){
            throw  new ResourceNotFoundException("Invalid Password");
        }

        authTokenRepository.expireAllUserTokens(user.getUsername());

        String token = jwtService.generateToken(user.getUsername());
        AuthTokenEntity authToken = new AuthTokenEntity();
        authToken.setUser(user);
        authToken.setToken(token);
        authToken.setCreatedAt(LocalDateTime.now());
        authToken.setExpired(false);

        authTokenRepository.save(authToken);

        return token;
    }
}
