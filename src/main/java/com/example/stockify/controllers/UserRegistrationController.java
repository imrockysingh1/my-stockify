package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.UserDTO;
import com.example.stockify.services.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import javax.print.DocFlavor;

@RestController()
@RequestMapping(path="/api/register")
public class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping(path = "")
    public ResponseEntity<ApiResponse<UserDTO>> userRegistration(@RequestBody @Valid UserDTO request){
       try {
           UserDTO savedUser = userRegistrationService.userRegistration(request);

           ApiResponse<UserDTO> response =
                   ApiResponse.<UserDTO>builder()
                           .success(true).
                           data(savedUser)
                           .build();

           return ResponseEntity.ok(response);
       }
       catch(Exception e){
           e.printStackTrace();
           ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                   .success(false)
                   .build();
           return ResponseEntity.status(500).body(response);
       }
    }
}
