package com.example.stockify.repositories;

import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByStatus(String status);

    List<OrderEntity> findByUsersUsername(String username);

    List<OrderEntity> findByUsersUsernameAndStatus(String username, String status);

    long countByUsers_Username(String username);

    long countByUsers_UsernameAndStatus(String username, String status);

    void deleteByUsersUsernameAndId(String username , Long id);
}