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
        UserEntity data = profileRepository.findUserWithAddressesAndWallet(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found "+username));

        ProfileDTO dto = modelMapper.map(data, ProfileDTO.class);
        dto.setAadhar(maskAadhar(dto.getAadhar()));
        dto.setPan(maskPan(dto.getPan()));

        return dto;
    }

    private String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4) return "****";
        return "**** **** " + aadhar.substring(aadhar.length() - 4);
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 10) return "*****";
        return pan.substring(0, 2) + "****" + pan.substring(9);
    }
}

