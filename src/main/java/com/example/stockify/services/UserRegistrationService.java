package com.example.stockify.services;

import com.example.stockify.dto.UserDTO;
import com.example.stockify.entities.AddressEntity;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.repositories.AddressRepository;
import com.example.stockify.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.util.List;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    public UserRegistrationService(UserRepository userRepository, ModelMapper modelMapper, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
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
        UserEntity toSaveEntity = modelMapper.map(request, UserEntity.class);
        UserEntity savedUser = userRepository.save(toSaveEntity);
        List<AddressEntity> savedAddress = request.getAddress().stream()
                .map(addressDTO -> {
                    AddressEntity addressEntity = modelMapper.map(addressDTO, AddressEntity.class);

                    addressEntity.setUser(toSaveEntity);

                    return addressRepository.save(addressEntity);
                })
                .toList();
        toSaveEntity.setAddresses(savedAddress);
        UserEntity savedEntity = userRepository.save(toSaveEntity);
        return modelMapper.map(savedEntity, UserDTO.class);
    }
}
