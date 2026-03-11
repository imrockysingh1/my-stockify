package com.example.stockify.services;

import com.example.stockify.dto.ProfileDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    public ProfileService(ProfileRepository profileRepository, ModelMapper modelMapper) {
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
    }

    public ProfileDTO getUserProfile(String username) {
        UserEntity data = profileRepository.findUserWithAddresses(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found "+username));
        return modelMapper.map(data, ProfileDTO.class);
    }
}

