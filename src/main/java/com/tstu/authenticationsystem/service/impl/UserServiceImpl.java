package com.tstu.authenticationsystem.service.impl;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.authenticationsystem.model.Role;
import com.tstu.authenticationsystem.model.User;
import com.tstu.authenticationsystem.repository.RoleRepository;
import com.tstu.authenticationsystem.repository.UserRepository;
import com.tstu.authenticationsystem.security.JwtTokenProvider;
import com.tstu.authenticationsystem.service.UserService;
import com.tstu.commons.exception.PrsException;
import com.tstu.commons.exception.PrsHttpException;
import com.tstu.commons.model.enums.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    @Override
    public String signIn(String username, String password) {

        try {
            log.info("Запрос на авторизацию пользователя - {}", username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new PrsHttpException(AuthenticationSystemErrors.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
            return jwtTokenProvider.createToken(username, user.getRoles());
        } catch (AuthenticationException e) {
            throw new PrsHttpException(AuthenticationSystemErrors.INVALID_USERNAME_OR_PASSWORD, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public String signUp(User user) {
        log.info("Регистрация нового пользователя - {}", user.getUsername());
        if(!userRepository.existsByUsername(user.getUsername())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(Status.ACTIVE);
            if(Objects.isNull(user.getRoles())) {
                Role userRole = roleRepository.findByName(Role.ROLE_USER)
                        .orElseThrow(() -> new PrsHttpException(AuthenticationSystemErrors.ROLE_NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY));
                user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            }
            User savedUser = userRepository.save(user);
            log.info("Пользователь зарегистрирован - {}", savedUser.getUsername());
            return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        } else {
            throw new PrsHttpException(AuthenticationSystemErrors.USERNAME_IS_ALREADY_IN_USE, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public void delete(String username) {
        log.info("Запрос на удаление пользователя - {}", username);
        userRepository.deleteByUsername(username);
    }

    @Override
    public User search(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new PrsHttpException(AuthenticationSystemErrors.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public User whoami(HttpServletRequest request) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request)))
                .orElseThrow(() -> new PrsException(AuthenticationSystemErrors.USER_NOT_FOUND));
    }

    @Override
    public User whoami(String token) {
        log.info("Запрос на поиска пользователя по токену - {}", token);
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(token)))
                .orElseThrow(() -> new PrsException(AuthenticationSystemErrors.USER_NOT_FOUND));
    }

    @Override
    public String refresh(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new PrsHttpException(AuthenticationSystemErrors.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        return jwtTokenProvider.createToken(username, user.getRoles());
    }

    @Override
    public boolean tokeIsValid(String token) {
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (PrsHttpException ex) {
            return false;
        }
    }
}
