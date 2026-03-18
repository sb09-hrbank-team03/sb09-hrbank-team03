package com.sb09.hrbank.exception;

import com.sb09.hrbank.dto.common.ErrorResponse;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e){
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "IllegalArgumentException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e){
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.NOT_FOUND.value(),
        "NoSuchElementException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleException(IllegalStateException e){
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.BAD_REQUEST.value(),
        "IllegalStateException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e){
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Exception",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

  }
}
