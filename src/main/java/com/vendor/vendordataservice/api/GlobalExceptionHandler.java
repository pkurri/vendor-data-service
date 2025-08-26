package com.vendor.vendordataservice.api;

import com.vendor.vendordataservice.api.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage("Validation failed");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        err.setDetails(details);
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage("Constraint violation");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setDetails(ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        err.setError("Internal Server Error");
        err.setMessage(ex.getMessage());
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
