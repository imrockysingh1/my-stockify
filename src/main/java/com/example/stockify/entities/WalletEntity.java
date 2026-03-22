package com.example.stockify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntity {

    @Id
    @Column(name = "username")
    private String username;

    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
    @JoinColumn(name = "username")
    private UserEntity user;

    @Column(name = "amount")
    @NotNull(message = "Amount should not be blank")
    @PositiveOrZero(message = "Amount cannot be negative")
    private Double amount;


}