package com.example.stockify.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {
    List<BuyOrderRequestDTO> orders;
    private long totalOrders;
    private long pendingOrders;
    private long executedOrders;
}
