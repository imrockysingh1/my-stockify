package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockServiceController {

    private final StockService stockService;

    public StockServiceController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<ApiResponse<StockResponseDTO>> getStock(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1d") String range,
            @RequestParam(defaultValue = "1m") String interval
    ) {

        StockResponseDTO data = stockService.getStock(symbol, range, interval);

        ApiResponse<StockResponseDTO> response =
                ApiResponse.<StockResponseDTO>builder()
                        .success(true)
                        .data(data)
                        .build();

        return ResponseEntity.ok(response);
    }
}