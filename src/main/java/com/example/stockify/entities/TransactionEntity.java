package com.example.stockify.entities;

import com.example.stockify.enums.TransactionType;
import com.example.stockify.enums.TradeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private UserEntity user;

    @Column(name = "stock_name", length = 100, nullable = false)
    private String stockName;

    // ✅ ENUM instead of String
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type; // BUY / SELL

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "txn_time", nullable = false, updatable = false)
    private LocalDateTime txnTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type")
    private TradeType tradeType; // DELIVERY / INTRADAY

    // ✅ Auto timestamp
    @PrePersist
    public void prePersist() {
        this.txnTime = LocalDateTime.now();
    }
}