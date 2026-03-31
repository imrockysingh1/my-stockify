package com.example.stockify.services;

import com.example.stockify.repositories.MarketHolidaysRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MarketHolidaysService {

    private final MarketHolidaysRepository repository;

    public MarketHolidaysService(MarketHolidaysRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "marketHolidays", key = "#date")
    public boolean isHoliday(LocalDate date) {
        return repository.existsByHolidayDate(date);
    }
}