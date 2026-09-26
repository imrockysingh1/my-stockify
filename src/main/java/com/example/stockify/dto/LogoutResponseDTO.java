package com.example.stockify.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutResponseDTO {

    private String username;
    private String token;
}
