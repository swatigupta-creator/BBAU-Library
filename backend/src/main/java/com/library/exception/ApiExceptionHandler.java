package com.library.exception;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(ApiException.class)
 ResponseEntity<Map<String,String>> handleApi(ApiException error) { return ResponseEntity.status(error.status).body(Map.of("error", error.getMessage())); }
 @ExceptionHandler(MethodArgumentNotValidException.class)
 ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException error) {
  FieldError field = error.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
  String message = field == null ? "Please check the submitted information." : field.getDefaultMessage();
  return ResponseEntity.badRequest().body(Map.of("error", message));
 }
 @ExceptionHandler(DuplicateKeyException.class)
 ResponseEntity<Map<String,String>> handleDuplicate(DuplicateKeyException error) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "An account with this email already exists.")); }
 @ExceptionHandler(Exception.class)
 ResponseEntity<Map<String,String>> handleUnexpected(Exception error) {
  System.err.println("Request failed: " + error.getMessage());
  return ResponseEntity.internalServerError().body(Map.of("error", "The library server could not complete this request."));
 }
}
