package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.LoginRequestDTO;
import com.example.stockify.dto.LoginResponseDTO;
import com.example.stockify.dto.UserDTO;
import com.example.stockify.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(path = "")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> userLogin(@RequestBody @Valid LoginRequestDTO data){
        String token = loginService.login(data);
        System.out.println("token"+token);
        LoginResponseDTO dto = new LoginResponseDTO(token);
        System.out.println("token after dto "+token);
        ApiResponse<LoginResponseDTO> response =
                ApiResponse.<LoginResponseDTO>builder()
                        .success(true)
                        .data(dto)
                        .build();

        return ResponseEntity.ok(response);

    }

}
