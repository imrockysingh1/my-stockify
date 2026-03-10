package com.example.stockify.services;

import com.example.stockify.dto.UserDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.entities.WalletEntity;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.repositories.WalletRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final WalletRepository walletRepository;
    private final EmailService emailService;

    public UserRegistrationService(UserRepository userRepository,
                                   ModelMapper modelMapper,
                                   WalletRepository walletRepository,
                                   EmailService emailService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.walletRepository = walletRepository;
        this.emailService = emailService;
    }

    @Transactional
    public UserDTO userRegistration(@Valid UserDTO request) {

        if (userRepository.existsById(request.getUsername()))
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByPan(request.getPan()))
            throw new RuntimeException("PAN already exists");
        if (userRepository.existsByAadhar(request.getAadhar()))
            throw new RuntimeException("Aadhar already exists");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already exists");
        if (userRepository.existsByPhone(request.getPhone()))
            throw new RuntimeException("Phone number already exists");

        // Save user FIRST
        UserEntity userEntity = modelMapper.map(request, UserEntity.class);
        userEntity.setEmailVerified(false);

        String otp = emailService.generateOtp();
        userEntity.setEmailOtp(otp);

        // Must flush to ensure user is in DB before wallet is created
        UserEntity savedEntity = userRepository.saveAndFlush(userEntity);

        // Create wallet AFTER user is saved
        WalletEntity wallet = new WalletEntity();
        wallet.setUsername(savedEntity.getUsername());  // set ID manually
        wallet.setAmount(10000.0f);
// DO NOT set user object - let JoinColumn handle it
        walletRepository.save(wallet);

        // Send OTP email
        try {
            emailService.sendOtpEmail(request.getEmail(), otp);
        } catch (Exception e) {
            System.out.println("Warning: Email sending failed - " + e.getMessage());
        }

        return modelMapper.map(savedEntity, UserDTO.class);
    }
}