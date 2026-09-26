package com.example.stockify.repositories;

import com.example.stockify.dto.LogoutResponseDTO;
import com.example.stockify.entities.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity,Long> {

    Optional<AuthTokenEntity> findByToken(String Token);
    @Query("""
            SELECT new com.example.stockify.dto.LogoutResponseDTO(
                a.user.username,
                a.token
            ) FROM AuthTokenEntity a
            WHERE a.token = :token
            """)
    Optional<LogoutResponseDTO> findLogoutDetailsByToken(
            @Param("token") String token
    );
    Optional<AuthTokenEntity> findByTokenAndIsExpiredFalse(String token);
    List<AuthTokenEntity> findByIsExpiredFalse();

    @Modifying
    @Query("""
            UPDATE AuthTokenEntity a SET a.isExpired = true WHERE a.user.username = :username AND a.isExpired=false
            """)
    void expireAllUserTokens(@Param("username") String username);
}
