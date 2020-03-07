package com.tstu.authenticationsystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tstu.commons.dto.http.response.error.ErrorResponse;
import com.tstu.commons.exception.PrsHttpException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class HttpResponseHelper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static HttpServletResponse formErrorResponse(HttpServletResponse response, PrsHttpException ex) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .id(UUID.randomUUID().toString())
                .time(LocalDateTime.now())
                .code(ex.getErrorCode().toString())
                .message(ex.getMessage())
                .displayMessage(ex.getMessage())
                .techInfo(null)
                .build();

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(objectMapper.writeValueAsString(errorResponse));
        response.setStatus(ex.getHttpStatus().value());
        return response;
    }
}
