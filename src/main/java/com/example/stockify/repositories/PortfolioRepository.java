package com.example.stockify.repositories;

import com.example.stockify.dto.PortfolioDTO;
import com.example.stockify.entities.PortfolioEntity;
import com.example.stockify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity , Integer> {

//    List<PortfolioEntity> findByUserUsername(String username);

    @Query("""
    SELECT new com.example.stockify.dto.PortfolioDTO(
        p.stockName,
        p.averagePrice,
        p.quantity,
        p.investment
    )
    FROM PortfolioEntity p
    WHERE p.user.username = :username
""")
    List<PortfolioDTO> findByUserUsername(String username);
    Optional<PortfolioEntity> findByUserAndStockName(UserEntity user, String stockName);
}

