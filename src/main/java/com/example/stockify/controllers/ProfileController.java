package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.ProfileDTO;
import com.example.stockify.repositories.ProfileRepository;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.services.JwtService;
import com.example.stockify.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping(path="api/user-profile")
public class ProfileController {
    private final ProfileService profileService ;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public ProfileController(ProfileService profileService, ProfileRepository profileRepository, UserRepository userRepository, JwtService jwtService) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getUserProfile(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {

//        String token = authHeader.substring(7);
//        String loggedInUser = jwtService.extractUsername(token);
//
//        if (!loggedInUser.equals(username)) {
//            throw new AccessDeniedException("You are not authorized to access this profile");
//        }

        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("You are not authorized");
        }

        ProfileDTO data = profileService.getUserProfile(username);

        ApiResponse<ProfileDTO> response =
                ApiResponse.<ProfileDTO>builder()
                        .success(true)
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }
}
