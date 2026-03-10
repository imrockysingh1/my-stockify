package com.example.stockify.repositories;

import com.example.stockify.entities.PortfolioEntity;
import com.example.stockify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , String> {

    Optional<Object> findAllByUsername(String username);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByAadhar(String aadhar);

    boolean existsByPan(String pan);
}
