package com.example.stockify.services;

import com.example.stockify.dto.UserDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public UserRegistrationService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDTO userRegistration(@Valid UserDTO request) {

        if(userRepository.existsById(request.getUsername())){
            throw new RuntimeException("Username Already exists");
        }
        if(userRepository.existsByPan(request.getPan())){
            throw new RuntimeException("PAN Already exists");
        }
        if(userRepository.existsByAadhar(request.getAadhar())){
            throw new RuntimeException("Aadhar Already exists");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email Already exists");
        }
        if(userRepository.existsByPhone(request.getPhone())){
            throw new RuntimeException("Phone number Already exists");
        }
        UserEntity tosaveEntity = modelMapper.map(request, UserEntity.class);
        UserEntity savedEntity = userRepository.save(tosaveEntity);
        return modelMapper.map(savedEntity, UserDTO.class);
    }
}
