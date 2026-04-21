package com.example.stockify.services;

import com.example.stockify.dto.StockCandleDTO;
import com.example.stockify.dto.StockMetaDTO;
import com.example.stockify.dto.StockResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    private final RestTemplate restTemplate = new RestTemplate();

    public StockResponseDTO getStock(String symbol, String range, String interval) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/"
                + symbol + "?range=" + range + "&interval=" + interval;

        Map<String, Object> response = null;
        int retryCount = 0;

        while (retryCount < 3) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();

                break;
            } catch (HttpServerErrorException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    retryCount++;
                    if (retryCount < 3) {
                        try {
                            Thread.sleep(2000 * retryCount);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        throw new RuntimeException("Too many requests. Please try again later.");
                    }
                } else {
                    throw new RuntimeException("Error fetching data: " + e.getMessage());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error fetching data: " + e.getMessage());
            }
        }

        if (response == null || response.get("chart") == null) {
            throw new RuntimeException("Unable to fetch valid response from Yahoo Finance API.");
        }

        Map chart = (Map) response.get("chart");
        List results = (List) chart.get("result");

        Map result = (Map) results.get(0);
        Map meta = (Map) result.get("meta");

        StockMetaDTO metaDTO = new StockMetaDTO();

        metaDTO.setSymbol((String) meta.get("symbol"));
        metaDTO.setName((String) meta.get("longName"));
        metaDTO.setCurrency((String) meta.get("currency"));
        metaDTO.setExchange((String) meta.get("fullExchangeName"));

        Double price = ((Number) meta.get("regularMarketPrice")).doubleValue();
        Double prev = ((Number) meta.get("previousClose")).doubleValue();

        metaDTO.setPrice(price);
        metaDTO.setPreviousClose(prev);
        metaDTO.setChange(price - prev);
        metaDTO.setChangePercent(((price - prev) / prev) * 100);

        metaDTO.setDayHigh(((Number) meta.get("regularMarketDayHigh")).doubleValue());
        metaDTO.setDayLow(((Number) meta.get("regularMarketDayLow")).doubleValue());
        metaDTO.setWeek52High(((Number) meta.get("fiftyTwoWeekHigh")).doubleValue());
        metaDTO.setWeek52Low(((Number) meta.get("fiftyTwoWeekLow")).doubleValue());
        metaDTO.setVolume(((Number) meta.get("regularMarketVolume")).longValue());

        List<Number> timestamps = (List<Number>) result.get("timestamp");

        Map indicators = (Map) result.get("indicators");
        List quotes = (List) indicators.get("quote");
        Map quote = (Map) quotes.get(0);

        List<Double> opens = (List<Double>) quote.get("open");
        List<Double> highs = (List<Double>) quote.get("high");
        List<Double> lows = (List<Double>) quote.get("low");
        List<Double> closes = (List<Double>) quote.get("close");
        List<Number> volumes = (List<Number>) quote.get("volume");
        List<StockCandleDTO> candles = new ArrayList<>();

        for (int i = 0; i < timestamps.size(); i++) {
            if (opens.get(i) == null) continue;

            Long time = timestamps.get(i).longValue();
            Long volume = (volumes.get(i) instanceof Integer)
                    ? ((Integer) volumes.get(i)).longValue()
                    : (volumes.get(i) != null ? ((Long) volumes.get(i)) : 0L);

            candles.add(new StockCandleDTO(
                    time,
                    opens.get(i),
                    highs.get(i),
                    lows.get(i),
                    closes.get(i),
                    volume
            ));
        }

        StockResponseDTO responseDTO = new StockResponseDTO();
        responseDTO.setMeta(metaDTO);
        responseDTO.setChart(candles);

        return responseDTO;
    }


    public StockResponseDTO getIndex(String symbol , String range, String interval) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/^"+symbol
                +"?range="+range+"&interval="+interval;
        System.out.println(url);
        Map <String , Object> response;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            ).getBody();
        }catch (Exception e) {
            throw new RuntimeException("Failed to fetch data: " + e.getMessage());
        }

        if (response == null || response.get("chart") == null) {
            throw new RuntimeException("Invalid response ");
        }

        Map chart = (Map) response.get("chart");
        List results = (List) chart.get("result");

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No data found");
        }

        Map result = (Map) results.get(0);
        Map meta = (Map) result.get("meta");

        // ✅ META
        StockMetaDTO metaDTO = new StockMetaDTO();

        metaDTO.setSymbol((String) meta.get("symbol"));
        metaDTO.setName((String) meta.get("longName"));
        metaDTO.setCurrency((String) meta.get("currency"));
        metaDTO.setExchange((String) meta.get("fullExchangeName"));

        Double price = getDouble(meta.get("regularMarketPrice"));
        Double prev = getDouble(meta.get("chartPreviousClose"));

        metaDTO.setPrice(price);
        metaDTO.setPreviousClose(prev);
        metaDTO.setChange(price - prev);
        metaDTO.setChangePercent(prev != 0 ? ((price - prev) / prev) * 100 : 0);

        metaDTO.setDayHigh(getDouble(meta.get("regularMarketDayHigh")));
        metaDTO.setDayLow(getDouble(meta.get("regularMarketDayLow")));
        metaDTO.setWeek52High(getDouble(meta.get("fiftyTwoWeekHigh")));
        metaDTO.setWeek52Low(getDouble(meta.get("fiftyTwoWeekLow")));
        metaDTO.setVolume(getLong(meta.get("regularMarketVolume")));

        // ✅ CHART
        List<Number> timestamps = (List<Number>) result.get("timestamp");

        Map indicators = (Map) result.get("indicators");
        List quotes = (List) indicators.get("quote");
        Map quote = (Map) quotes.get(0);

        List<Number> opens = (List<Number>) quote.get("open");
        List<Number> highs = (List<Number>) quote.get("high");
        List<Number> lows = (List<Number>) quote.get("low");
        List<Number> closes = (List<Number>) quote.get("close");
        List<Number> volumes = (List<Number>) quote.get("volume");

        List<StockCandleDTO> candles = new ArrayList<>();

        for (int i = 0; i < timestamps.size(); i++) {

            if (opens.get(i) == null || closes.get(i) == null) continue;

            candles.add(new StockCandleDTO(
                    timestamps.get(i).longValue(),
                    opens.get(i).doubleValue(),
                    highs.get(i) != null ? highs.get(i).doubleValue() : 0.0,
                    lows.get(i) != null ? lows.get(i).doubleValue() : 0.0,
                    closes.get(i).doubleValue(),
                    volumes.get(i) != null ? volumes.get(i).longValue() : 0L
            ));
        }

        StockResponseDTO responseDTO = new StockResponseDTO();
        responseDTO.setMeta(metaDTO);
        responseDTO.setChart(candles);

        return responseDTO;
    }

    private Double getDouble(Object value) {
        return value != null ? ((Number) value).doubleValue() : 0.0;
    }

    private Long getLong(Object value) {
        return value != null ? ((Number) value).longValue() : 0L;
    }
}
