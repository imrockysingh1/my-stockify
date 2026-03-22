package com.example.stockify.services;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.example.stockify.dto.UserDTO;
import com.example.stockify.entities.AddressEntity;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.entities.WalletEntity;
import com.example.stockify.repositories.AddressRepository;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.repositories.WalletRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.net.DatagramPacket;
import java.util.List;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final WalletRepository walletRepository;
    private final EmailService emailService;

    public UserRegistrationService(UserRepository userRepository, ModelMapper modelMapper, AddressRepository addressRepository, WalletRepository walletRepository,EmailService emailService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.walletRepository = walletRepository;
        this.emailService = emailService;
    }
    @Transactional
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
        UserEntity userEntity = modelMapper.map(request, UserEntity.class);
        userEntity.setEmailVerified(false);
        if (request.getAddress() != null) {
            userEntity.setAddresses(new ArrayList<>());
        }
        String otp = emailService.generateOtp();
        userEntity.setEmailOtp(otp);

        UserEntity savedUser = userRepository.saveAndFlush(userEntity);
        if (request.getAddress() != null) {
            List<AddressEntity> savedAddresses = request.getAddress().stream()
                    .map(addressDTO -> {
                        AddressEntity addressEntity = modelMapper.map(addressDTO, AddressEntity.class);
                        addressEntity.setUser(savedUser);
                        return addressRepository.save(addressEntity);
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
            savedUser.setAddresses(savedAddresses);
//            userRepository.save(savedUser);
        }

        // Create wallet AFTER user is saved (MapsId — set user, not username)
        WalletEntity wallet = new WalletEntity();
        wallet.setUsername(savedUser.getUsername());
        wallet.setAmount(10000.0);
        walletRepository.save(wallet);

        // Send OTP email (non-blocking)
        try {
            emailService.sendOtpEmail(request.getEmail(), otp);
        } catch (Exception e) {
            System.out.println("Warning: Email sending failed - " + e.getMessage());
        }

        return modelMapper.map(savedUser, UserDTO.class);
    }
}
