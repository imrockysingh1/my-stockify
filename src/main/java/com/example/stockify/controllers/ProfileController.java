package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.ProfileDTO;
import com.example.stockify.dto.UpdateProfileDTO;
import com.example.stockify.dto.WalletDTO;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.repositories.ProfileRepository;
import com.example.stockify.repositories.UserRepository;
import com.example.stockify.services.JwtService;
import com.example.stockify.services.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.stockify.services.EmailService;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequestMapping(path="api/user-profile")
public class ProfileController {
    private final ProfileService profileService ;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    public ProfileController(ProfileService profileService, ProfileRepository profileRepository, UserRepository userRepository, JwtService jwtService,EmailService emailService) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getUserProfile(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {

        validateUser(authHeader, username);
        ProfileDTO data = profileService.getUserProfile(username);
        return ResponseEntity.ok(ApiResponse.<ProfileDTO>builder().success(true).data(data).build());
    }

    // ── UPDATE Profile ───────────────────────────────────────────
    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileDTO updateDTO
    ) throws AccessDeniedException {
        validateUser(authHeader, username);
        ProfileDTO data = profileService.updateUserProfile(username, updateDTO);
        return ResponseEntity.ok(ApiResponse.<ProfileDTO>builder().success(true).data(data).build());
    }

    // ── GET Wallet Balance ───────────────────────────────────────
    @GetMapping("/{username}/wallet")
    public ResponseEntity<ApiResponse<WalletDTO>> getWallet(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {
        validateUser(authHeader, username);
        WalletDTO wallet = profileService.getWalletBalance(username);
        return ResponseEntity.ok(ApiResponse.<WalletDTO>builder().success(true).data(wallet).build());
    }

    // ── Send OTP ─────────────────────────────────────────────────
    @PostMapping("/{username}/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {
        validateUser(authHeader, username);

        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = emailService.generateOtp();
        user.setEmailOtp(otp);
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<String>builder()
                    .success(false).data("Failed to send email: " + e.getMessage()).build());
        }

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true).data("OTP sent to " + user.getEmail()).build());
    }

    // ── Verify Email OTP ─────────────────────────────────────────
    @PostMapping("/{username}/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @PathVariable String username,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body
    ) throws AccessDeniedException {
        validateUser(authHeader, username);

        String otp = body.get("otp");
        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getEmailOtp() != null && user.getEmailOtp().equals(otp)) {
            user.setEmailVerified(true);
            user.setEmailOtp(null);
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true).data("Email verified successfully!").build());
        }

        return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                .success(false).data("Invalid OTP. Please try again.").build());
    }

    // ── Auth Helper ───────────────────────────────────────────────
    private void validateUser(String authHeader, String username) throws AccessDeniedException {
        String token = authHeader.substring(7);
        String loggedInUser = jwtService.extractUsername(token);
        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("Not authorized to access this profile");
        }
    }
}
