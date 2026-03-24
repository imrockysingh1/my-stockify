package com.example.stockify.services;

import com.example.stockify.dto.ProfileDTO;
import com.example.stockify.dto.UpdateProfileDTO;
import com.example.stockify.dto.WalletDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.entities.WalletEntity;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.repositories.ProfileRepository;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.repositories.WalletRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public ProfileService(ProfileRepository profileRepository,
                          ModelMapper modelMapper,
                          UserRepository userRepository,
                          WalletRepository walletRepository) {
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
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

    // UPDATE profile (phone, email, fatherName, occupation, maritalStatus, income)
    public ProfileDTO updateUserProfile(String username, UpdateProfileDTO dto) {
        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found " + username));

        if (dto.getPhone() != null && !dto.getPhone().isBlank())
            user.setPhone(dto.getPhone());

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
            user.setEmailVerified(false); // must re-verify new email
        }

        if (dto.getFatherName() != null) user.setFatherName(dto.getFatherName());
        if (dto.getOccupation() != null) user.setOccupation(dto.getOccupation());
        if (dto.getMaritalStatus() != null) user.setMaritalStatus(dto.getMaritalStatus());
        if (dto.getIncome() != null) user.setIncome(dto.getIncome());

        UserEntity saved = userRepository.save(user);
        return modelMapper.map(saved, ProfileDTO.class);
    }

    // GET wallet balance
    public WalletDTO getWalletBalance(String username) {
        WalletEntity wallet = walletRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for " + username));
        return new WalletDTO(wallet.getUsername(), wallet.getAmount());
    }
}
