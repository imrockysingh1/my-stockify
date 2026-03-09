package com.example.stockify.dto;

import com.example.stockify.annotation.AdultValidation;
import com.example.stockify.annotation.PasswordValidation;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message = "DOB is required")
    @AdultValidation(message = "Age should be 18 or above")
    private LocalDate dob;

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar must be 12 digits")
    private String aadhar;

    @NotBlank(message = "PAN is required")
    private String pan;

    @PositiveOrZero(message = "Income cannot be negative")
    private Double income;

    @NotNull(message = "Password is required")
    @PasswordValidation(message = "Password must be strong  (A-Z,a-z,0-1,(#,$@%...),8chars")
    private String password;
}