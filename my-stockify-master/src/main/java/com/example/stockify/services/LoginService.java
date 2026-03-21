package com.example.stockify.services;

import com.example.stockify.dto.LoginRequestDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String login(@Valid LoginRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid Username"));

        if(!user.getPassword().equals(request.getPassword())){
            throw  new ResourceNotFoundException("Invalid Password");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
