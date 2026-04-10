package com.example.stockify.entities;

import com.example.stockify.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name= "orders"
)
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private Integer quantity;
    private String orderType;
    private Double price;
    private String status;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="username" , referencedColumnName = "username")
    private UserEntity users;

}
