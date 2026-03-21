package com.example.stockify.repositories;

import com.example.stockify.entities.TransactionEntity;
import com.example.stockify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findByUser(UserEntity username);
}