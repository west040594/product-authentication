package com.tstu.authenticationsystem.security;

import com.tstu.authenticationsystem.utils.HttpResponseHelper;
import com.tstu.commons.exception.PrsHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        try {
            if(token != null && jwtTokenProvider.validateToken(token)){
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if(authentication != null ) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (PrsHttpException ex) {
            SecurityContextHolder.clearContext();
            HttpResponseHelper.formErrorResponse(response, ex);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
