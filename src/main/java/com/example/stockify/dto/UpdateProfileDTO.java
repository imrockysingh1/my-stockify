package com.example.stockify.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Email
    private String email;

    private String fatherName;
    private String occupation;
    private String maritalStatus;

    @PositiveOrZero(message = "Income cannot be negative")
    private Double income;
}
