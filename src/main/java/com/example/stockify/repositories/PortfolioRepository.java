package com.example.stockify.repositories;

import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.entities.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity , Long> {
    List<PortfolioEntity> findByUserUsername(String username) ;
}
