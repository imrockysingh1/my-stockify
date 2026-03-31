package com.example.stockify.services;

import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class MarketTimeService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final LocalTime OPEN = LocalTime.of(9, 15);
    private static final LocalTime CLOSE = LocalTime.of(15, 30);

    private final MarketHolidaysService holidayService;

    public MarketTimeService(MarketHolidaysService holidayService) {
        this.holidayService = holidayService;
    }

    public boolean isMarketOpen() {
        ZonedDateTime now = ZonedDateTime.now(ZONE);
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();
        DayOfWeek day = now.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }

        if (holidayService.isHoliday(date)) {
            System.out.println("is holiday"+date);
            return false;
        }

        return !time.isBefore(OPEN) && !time.isAfter(CLOSE);
    }
}