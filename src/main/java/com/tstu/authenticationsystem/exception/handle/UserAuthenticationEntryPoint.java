package com.tstu.authenticationsystem.exception.handle;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.authenticationsystem.utils.HttpResponseHelper;
import com.tstu.commons.exception.PrsHttpException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponseHelper.formErrorResponse(response, new PrsHttpException(AuthenticationSystemErrors.ACCESS_DENIED, HttpStatus.FORBIDDEN));
    }
}
