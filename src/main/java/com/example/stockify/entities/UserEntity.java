package com.example.stockify.entities;

import com.example.stockify.annotation.AdultValidation;
import com.example.stockify.annotation.PasswordValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone"),
                @UniqueConstraint(columnNames = "aadhar"),
                @UniqueConstraint(columnNames = "pan")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "username", length = 50)
    @NotNull(message = "username is required")
    private String username;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Column(name = "dob")
    @AdultValidation(message = "Age should be 18 or above")
    private LocalDate dob;

    @Column(name = "aadhar", length = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar must be 12 digits")
    private String aadhar;

    @Column(name = "pan", length = 10)
    @NotBlank(message = "PAN is required")
    private String pan;

    @Column(name = "income")
    @Positive(message = "Income should not be negative")
    private Double income;

    @Column(name = "password")
    @PasswordValidation(message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;

    // ── NEW FIELDS ───────────────────────────────────────
    @Column(name = "father_name", length = 60)
    private String fatherName;

    @Column(name = "occupation", length = 60)
    private String occupation;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "email_otp", length = 6)
    private String emailOtp;
}
