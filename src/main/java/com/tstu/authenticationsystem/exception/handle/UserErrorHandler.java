package com.tstu.authenticationsystem.exception.handle;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.commons.dto.http.response.error.ErrorResponse;
import com.tstu.commons.exception.handle.PrsErrorHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class UserErrorHandler extends PrsErrorHandler {


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .id(UUID.randomUUID().toString())
                .time(LocalDateTime.now())
                .code(AuthenticationSystemErrors.JSON_NOT_READABLE.name())
                .message(ex.getMessage())
                .displayMessage(AuthenticationSystemErrors.JSON_NOT_READABLE.getErrorDescription())
                .techInfo(null)
                .build();
        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }

}
