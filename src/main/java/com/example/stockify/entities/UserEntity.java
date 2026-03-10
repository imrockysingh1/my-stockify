package com.example.stockify.entities;

import com.example.stockify.annotation.AdultValidation;
import com.example.stockify.annotation.PasswordValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

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


    @Column(name = "name" , length = 30)
    private String name;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "phone")
    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be 10 digits"
    )
    private String phone;

    @Column(name = "dob")
    @AdultValidation(message = "Age should be 18 or above")
    private LocalDate dob;

    @Column(name = "maratial_status")
    @NotBlank(message = "Maratial should not be blank")
    @Pattern(
            regexp = "^(Single|Married|Divorced|Widowed)$",
            message = "Marital Status must be one of: Single, Married, Divorced, Widowed"
    )
    private String maratialStatus;

    @Column(name = "gender")
    @NotBlank(message = "Gender should not be blank")
    @Pattern(
            regexp = "^(Male|Female|Other)$",
            message = "Gender must be Male, Female, or Other"
    )
    private String gender;

    @Column(name = "aadhar", length = 12)
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar must be 12 digits")
    private String aadhar;


    @Column(name = "pan" , length = 10)
    @NotBlank(message = "PAN is required")
    private String pan;

    @Column(name = "income_range")
    @NotBlank(message = "Income range is required")
    @Pattern(
            regexp = "^(Upto 1L|1-5L|5-10L|10-25L|25-50L|50L-1Cr)$",
            message = "Income range must be one of: Upto 1L, 1-5L, 5-10L, 10-25L, 25-50L, 50L-1Cr"
    )
    private String incomeRange;

    @Column(name = "occupation")
    @NotBlank(message = "Occupation is required")
    @Pattern(
            regexp = "^(Salaried|Self Employed|Student|Business|Retired|Unemployed)$",
            message = "Occupation must be one of: Salaried, Self Employed, Student, Business, Retired, Unemployed"
    )
    private String occupation;

    @Column(name = "father_name", length = 50)
    @NotBlank(message = "Father's name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Father's name should contain only alphabets and spaces"
    )
    @Size(max = 50, message = "Father's name cannot exceed 50 characters")
    private String fatherName;

    @Column(name = "password")
    @PasswordValidation( message = "Password must be at least 8 characters long and include at least one uppercase letter, " +
                                   "one lowercase letter, one number, and one special character.")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressEntity> addresses;

}