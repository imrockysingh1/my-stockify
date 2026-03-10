package com.example.stockify.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private UserEntity user;

    @Column(name = "stockName", length = 100)
    @NotBlank(message = "Stock name is required")
    private String stockName;

    @Column(name = "type", length = 20)
    @NotBlank(message = "Transaction type is required")
    private String type;

    @Column(name = "quantity")
    @Positive(message = "Quantity must be positive")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Column(name = "price")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Float price;

    @Column(name = "amount")
    @PositiveOrZero
    private Float amount;

    @Column(name = "txn_time", insertable = false, updatable = false)
    private LocalDateTime txnTime;

    @Column(name = "trade_type", length = 20)
    private String tradeType;

}