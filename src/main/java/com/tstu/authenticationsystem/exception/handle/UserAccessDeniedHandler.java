package com.tstu.authenticationsystem.exception.handle;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.authenticationsystem.utils.HttpResponseHelper;
import com.tstu.commons.exception.PrsHttpException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponseHelper.formErrorResponse(response, new PrsHttpException(AuthenticationSystemErrors.ACCESS_DENIED, HttpStatus.FORBIDDEN));
    }
}
