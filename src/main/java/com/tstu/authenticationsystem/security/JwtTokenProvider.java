package com.tstu.authenticationsystem.security;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.authenticationsystem.model.Role;
import com.tstu.commons.exception.PrsHttpException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:west223}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validityInMilliseconds = 3600000L;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream().map(role -> new SimpleGrantedAuthority(role.getAuthority())).filter(Objects::nonNull).collect(Collectors.toList()));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        log.info("Expire Millesecond - {}", validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return resolveToken(bearerToken);
    }

    public String resolveToken(String bearerToken) {
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e) {
            throw new PrsHttpException(AuthenticationSystemErrors.EXPIRED_OR_INVALID_JWT_TOKEN, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
