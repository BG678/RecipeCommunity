package com.recipecommunity.utils;

import com.recipecommunity.features.jwt.UsernameIsAlreadyTakenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "Request method '" + ex.getMethod() + "' not supported.";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ApiExceptionResponse(
                status.getReasonPhrase(), ex.getMessage(), status.value()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiExceptionResponse
                (status.getReasonPhrase(), ex.getMessage(), status.value()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable
            (HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = "Provide Request Body in valid JSON format";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiExceptionResponse
                (status.getReasonPhrase(), message, status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UsernameIsAlreadyTakenException.class)
    @ResponseBody
    public ResponseEntity<ApiExceptionResponse> handleUsernameIsAlreadyTakenException(UsernameIsAlreadyTakenException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ApiExceptionResponse
                (status.getReasonPhrase(), exception.getMessage(), status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<ApiExceptionResponse> handleBadCredentialsException(BadCredentialsException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new ApiExceptionResponse
                (status.getReasonPhrase(), exception.getMessage(), status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<ApiExceptionResponse> handleAccessDeniedException(AccessDeniedException exn) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiExceptionResponse
                (status.getReasonPhrase(), exn.getMessage(), status.value()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(PageDoesNotExist.class)
    @ResponseBody
    ResponseEntity<ApiExceptionResponse> handlePageDoesNotExist(PageDoesNotExist ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(new ApiExceptionResponse
                (status.getReasonPhrase(), ex.getMessage(), status.value()));
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    ResponseEntity<ApiExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(new ApiExceptionResponse
                (status.getReasonPhrase(), ex.getMessage(), status.value()));
    }
}
