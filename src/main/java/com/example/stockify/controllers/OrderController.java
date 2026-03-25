package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.dto.UserDTO;
import com.example.stockify.services.JwtService;
import com.example.stockify.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/orders")   // ✅ base path
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    private final JwtService jwtService;

    @PostMapping("/buy")   // ✅ full path = /api/orders/buy
    public ResponseEntity<ApiResponse<BuyOrderRequestDTO>> buyStock(
            @RequestParam String username,
            @RequestBody BuyOrderRequestDTO request,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {
//        String token = authHeader.substring(7);
//        String loggedInUser = jwtService.extractUsername(token);
//
//        if (!loggedInUser.equals(username)) {
//            throw new AccessDeniedException("You are not authorized to access this profile");
//        }
        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("You are not authorized");
        }

//        orderService.buyStock(
//                username,
//                request.getSymbol(),
//                request.getQuantity(),
//                request.getOrderType(),
//                request.getPrice()
//        );

        BuyOrderRequestDTO savedOrder =  orderService.buyStock(
                username,
                request
        );

        ApiResponse<BuyOrderRequestDTO> response =
                ApiResponse.<BuyOrderRequestDTO>builder()
                        .success(true).
                        data(savedOrder)
                        .build();

        return ResponseEntity.ok(response);

//        return ResponseEntity.ok("Order placed successfully");
    }
}