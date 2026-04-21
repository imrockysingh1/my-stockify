package com.example.stockify.controllers;

import com.example.stockify.advice.ApiResponse;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockServiceController {

    private static final Map<String, String> SYMBOL_MAP = Map.of(
            "nifty50", "NSEI",
            "bank-nifty", "NSEBANK",
            "finnifty", "NSEFIN",
            "nifty-it", "CNXIT",
            "nifty-midcap-50", "NSEMDCP50",
            "nifty-smallcap", "CNXSMLCAP",
            "sensex-bse", "BSESN",
            "india-vix", "INDIAVIX"
    );

    private final StockService stockService;
    private final PathPatternRequestMatcher.Builder builder;

    public StockServiceController(StockService stockService, PathPatternRequestMatcher.Builder builder) {
        this.stockService = stockService;
        this.builder = builder;
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

    @GetMapping("/index/{symbol}")
    public ResponseEntity<ApiResponse<StockResponseDTO>> getIndex(
            @PathVariable String symbol,
            @RequestParam(required = false , defaultValue = "1d") String range,
            @RequestParam(required = false , defaultValue = "1m") String interval
    ){
        String mappedSymbol = SYMBOL_MAP.get(symbol.toLowerCase());

        if (mappedSymbol == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<StockResponseDTO>builder()
                            .success(false)
                            .message("Invalid symbol: " + symbol)
                            .build());
        }

        System.out.println(mappedSymbol);
        StockResponseDTO data = stockService.getIndex(mappedSymbol, range, interval);

        ApiResponse<StockResponseDTO> response = ApiResponse.<StockResponseDTO>builder()
                .success(true)
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

}