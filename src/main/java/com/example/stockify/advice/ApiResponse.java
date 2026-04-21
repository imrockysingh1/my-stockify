package com.example.stockify.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    private boolean success;

    private T data;

    private ApiError error;

    private String message;
}