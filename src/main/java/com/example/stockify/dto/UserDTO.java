package com.example.stockify.dto;

import com.example.stockify.annotation.AdultValidation;
import com.example.stockify.annotation.PasswordValidation;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    @NotBlank(message = "Maratial should not be blank")
    @Pattern(
            regexp = "^(Single|Married|Divorced|Widowed)$",
            message = "Marital Status must be one of: Single, Married, Divorced, Widowed"
    )
    private String maratialStatus;

    @NotBlank(message = "Gender should not be blank")
    @Pattern(
            regexp = "^(Male|Female|Other)$",
            message = "Gender must be Male, Female, or Other"
    )
    private String gender;

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar must be 12 digits")
    private String aadhar;

    @NotBlank(message = "PAN is required")
    private String pan;


    @NotBlank(message = "Income range is required")
    @Pattern(
            regexp = "^(Upto 1L|1-5L|5-10L|10-25L|25-50L|50L-1Cr)$",
            message = "Income range must be one of: Upto 1L, 1-5L, 5-10L, 10-25L, 25-50L, 50L-1Cr"
    )
    private String incomeRange;

    @NotBlank(message = "Occupation is required")
    @Pattern(
            regexp = "^(Salaried|Self Employed|Student|Business|Retired|Unemployed)$",
            message = "Occupation must be one of: Salaried, Self Employed, Student, Business, Retired, Unemployed"
    )
    private String occupation;

    @NotBlank(message = "Father's name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Father's name should contain only alphabets and spaces"
    )
    @Size(max = 50, message = "Father's name cannot exceed 50 characters")
    private String fatherName;

    @NotNull(message = "Password is required")
    @PasswordValidation(message = "Password must be strong (A-Z, a-z, 0-9, special chars, 8+ chars)")
    private String password;

    @NotEmpty(message = "Address is required")
    private List<AddressDTO> address;

    private WalletDTO wallet;

    private String maritalStatus;
    private Boolean emailVerified;
}