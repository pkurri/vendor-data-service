package com.vendor.vendordataservice.api;

import com.vendor.vendordataservice.api.dto.ErrorResponse;
import com.vendor.vendordataservice.api.error.ApiBadRequestException;
import com.vendor.vendordataservice.api.error.DuplicateRequestIdException;
import com.vendor.vendordataservice.api.error.TooManyRequestsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static String requestId(WebRequest request) {
        try {
            return ((ServletWebRequest)request).getRequest().getHeader("X-Request-Id");
        } catch (Exception e) {
            return null;
        }
    }

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
        err.setErrorCode("UNPROCESSABLE_ENTITY");
        err.setRequestId(requestId(request));
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
        err.setErrorCode("UNPROCESSABLE_ENTITY");
        err.setRequestId(requestId(request));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(ApiBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleApiBadRequest(ApiBadRequestException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage(ex.getMessage());
        err.setErrorCode(ex.getErrorCode());
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        if (ex.getDetails() != null) err.setDetails(ex.getDetails());
        err.setRequestId(requestId(request));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage("Field 'request_id' is required (header X-Request-Id)");
        err.setErrorCode("MISSING_REQUEST_ID");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setRequestId(requestId(request));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage("Invalid parameter format");
        err.setErrorCode("INVALID_DATE_FORMAT");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setRequestId(requestId(request));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(DuplicateRequestIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateReqId(DuplicateRequestIdException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.CONFLICT.value());
        err.setError("Conflict");
        err.setMessage(ex.getMessage());
        err.setErrorCode("DUPLICATE_REQUEST_ID");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setRequestId(requestId(request));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooMany(TooManyRequestsException ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(429);
        err.setError("Too Many Requests");
        err.setMessage(ex.getMessage());
        err.setErrorCode("RATE_LIMIT_EXCEEDED");
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setRequestId(requestId(request));
        return ResponseEntity.status(429)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, WebRequest request) {
        ErrorResponse err = new ErrorResponse();
        err.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        err.setError("Internal Server Error");
        err.setMessage(ex.getMessage());
        err.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
        err.setRequestId(requestId(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
