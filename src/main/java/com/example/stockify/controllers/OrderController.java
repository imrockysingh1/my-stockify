package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.dto.OrderListDTO;
import com.example.stockify.dto.UserDTO;
import com.example.stockify.services.JwtService;
import com.example.stockify.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")   // ✅ base path
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    private final JwtService jwtService;

    @GetMapping("/get-orders")
    public ResponseEntity<ApiResponse<OrderListDTO>> getOrders(
            @RequestParam String username,
            @RequestParam(required = false , defaultValue = "ALL") String orderType
    )throws AccessDeniedException{
        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("You are not authorized");
        }

        OrderListDTO orders = orderService.getOrders(username, orderType);

        ApiResponse<OrderListDTO> response =
                ApiResponse.<OrderListDTO>builder()
                        .success(true)
                        .data(orders)
                        .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/buy")   // ✅ full path = /api/orders/buy
    public ResponseEntity<ApiResponse<BuyOrderRequestDTO>> buyStock(
            @RequestParam String username,
            @RequestBody BuyOrderRequestDTO request,
            @RequestHeader("Authorization") String authHeader
    ) throws AccessDeniedException {
        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!loggedInUser.equals(username)) {
            throw new AccessDeniedException("You are not authorized");
        }
        BuyOrderRequestDTO savedOrder =  orderService.buyStock(username,request);
        ApiResponse<BuyOrderRequestDTO> response =
                ApiResponse.<BuyOrderRequestDTO>builder()
                        .success(true).
                        data(savedOrder)
                        .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<BuyOrderRequestDTO>> sellStock(
            @RequestParam String username ,
            @RequestBody BuyOrderRequestDTO request,
            @RequestHeader("Authorization") String authHeader
    )throws AccessDeniedException{
        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if(!loggedInUser.equals(username))
            throw new AccessDeniedException(("You are not Authenticated "));

        BuyOrderRequestDTO savedOrders = orderService.sellStock(username , request);
        ApiResponse<BuyOrderRequestDTO> response =
                ApiResponse.<BuyOrderRequestDTO>builder()
                        .success(true).
                        data(savedOrders)
                        .build();

        return ResponseEntity.ok(response);


    }

    @DeleteMapping("/delete-orders")
    public ResponseEntity<ApiResponse<Void>> deleteStock(
            @RequestParam String username ,
            @RequestParam Long id
    )throws AccessDeniedException{
        String loggedInUser = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if(!loggedInUser.equals(username))
            throw new AccessDeniedException(("You are not Authenticated "));
        orderService.deleteStocks(username,id);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(true)
                        .data(null)
                        .build();

        return ResponseEntity.ok(response);
    }

}