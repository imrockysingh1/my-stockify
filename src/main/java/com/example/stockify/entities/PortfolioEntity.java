package com.example.stockify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "portfolio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "stockName", length = 100)
    @NotBlank(message = "Stock name is required")
    private String stockName;

    @Column(name = "averagePrice")
    @PositiveOrZero(message = "Average price cannot be negative")
    @NotNull(message = "Average price is required")
    private Float averagePrice;

    @Column(name = "quantity")
    @PositiveOrZero(message = "Quantity cannot be negative")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Column(name = "investment")
    @PositiveOrZero(message = "Investment cannot be negative")
    private Float investment;

    @PrePersist
    @PreUpdate
    public void calculateInvestment() {
        if (averagePrice != null && quantity != null) {
            this.investment = averagePrice * quantity;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private UserEntity user;

}