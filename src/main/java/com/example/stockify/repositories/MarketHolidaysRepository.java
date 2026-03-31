package com.example.stockify.repositories;

import com.example.stockify.entities.MarketHolidays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MarketHolidaysRepository extends JpaRepository<MarketHolidays, Long> {

    boolean existsByHolidayDate(LocalDate date);

}