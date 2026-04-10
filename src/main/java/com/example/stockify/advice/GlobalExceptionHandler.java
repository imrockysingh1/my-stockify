package com.example.stockify.advice;

import com.example.stockify.exception.InsufficientBalanceException;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.exception.StockDataNotFoundException;
import com.example.stockify.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException e){
        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception e){
        ApiError apiError = ApiError
                .builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputvalidation(MethodArgumentNotValidException e){
        List<String> errors = e
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error->error.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = ApiError
                .builder().status(HttpStatus.BAD_REQUEST)
                .message("Input Validation failed\n"+errors)
                .build();

        return buildApiResponseEntity(apiError);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientBalance(InsufficientBalanceException e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.PAYMENT_REQUIRED)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(apiError);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleWalletNotFoundException(WalletNotFoundException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(apiError);
    }

    @ExceptionHandler(StockDataNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleStockDataNotFoundException(WalletNotFoundException e){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException e) {

        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build();

        return buildApiResponseEntity(error);
    }

    private ResponseEntity<ApiResponse<?>> buildApiResponseEntity(ApiError error) {

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .error(error)
                .build();

        return new ResponseEntity<>(response, error.getStatus());
    }
}
