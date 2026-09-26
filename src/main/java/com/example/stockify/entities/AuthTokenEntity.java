package com.example.stockify.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_token_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "username",
            referencedColumnName = "username",
            nullable = false
    )private UserEntity user;

    @Column(nullable = false , length = 1000)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired = false;

}
