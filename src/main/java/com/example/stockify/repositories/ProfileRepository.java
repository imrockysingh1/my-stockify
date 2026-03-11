package com.example.stockify.repositories;

import com.example.stockify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<UserEntity , String> {
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.addresses WHERE u.username = :username")
    Optional<UserEntity> findUserWithAddresses(@Param("username") String username);
}
