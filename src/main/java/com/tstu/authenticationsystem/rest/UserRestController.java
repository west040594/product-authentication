package com.tstu.authenticationsystem.rest;

import com.tstu.authenticationsystem.exception.AuthenticationExceptionMessage;
import com.tstu.authenticationsystem.mapping.UserMapper;
import com.tstu.authenticationsystem.model.User;
import com.tstu.authenticationsystem.service.RoleService;
import com.tstu.authenticationsystem.service.UserService;
import com.tstu.commons.dto.http.request.authenticationsystem.AuthenticationRequest;
import com.tstu.commons.dto.http.request.authenticationsystem.UserDataRequest;
import com.tstu.commons.dto.http.response.authenticationsystem.AuthenticationResponse;
import com.tstu.commons.dto.http.response.authenticationsystem.UserResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {


    private final UserMapper userMapper;
    private final UserService userService;
    private final RoleService roleService;

    /**
     * Авторизация пользователя по логину и паролю
     * @param authRequest Структура которая содержит логин и пароль
     * @return Структура со всей информацией о пользователе и токеном аутентификации
     */
    @PostMapping("/signin")
    @ApiOperation(value = "${api.swagger.user.signin}", response = AuthenticationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 422, message = AuthenticationExceptionMessage.INVALID_USERNAME_OR_PASSWORD_MSG)
    })
    public ResponseEntity<?> login(
            @ApiParam("Authentication Request") @RequestBody AuthenticationRequest authRequest) {
        String token = userService.signIn(authRequest.getUsername(), authRequest.getPassword());
        User user = userService.search(authRequest.getUsername());
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return ResponseEntity.ok(new AuthenticationResponse(userResponse, token));
    }

    /**
     * Регистрация нового пользователя
     * @param userDataRequest Структура которая содержит всю необходимую информация для создания нового пользователя
     * @return Структура со всей информацией о пользователе и токеном аутентификации
     */
    @PostMapping("/signup")
    @ApiOperation(value = "${api.swagger.user.signup}", response = AuthenticationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 422, message = AuthenticationExceptionMessage.UNABLE_TO_PROCESS_DATA),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> signup(@ApiParam("Signup User") @RequestBody UserDataRequest userDataRequest) {
        String token = userService.signUp(userMapper.userDataRequestToUser(userDataRequest, roleService));
        User user = userService.search(userDataRequest.getUsername());
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return ResponseEntity.ok(new AuthenticationResponse(userResponse, token));
    }

    /**
     * Удаление пользователя по его username. Данный метод может выполнить только пользователь с правами ROLE_ADMIN
     * @param username Имя пользователя
     * @return Строка информации о успешном удалении
     */
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${api.swagger.user.delete}", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 404, message = AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> delete(@ApiParam("Username") @PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(String.format("Пользователь %s - удален", username));
    }

    /**
     * Получить полную информацию о пользователе по его username. Данный метод может выполнить только пользователь с правами ROLE_ADMIN
     * @param username Имя пользователя
     * @return Структура с детальной информацией о пользователе
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${api.swagger.user.search}", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 404, message = AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> search(@ApiParam("Username") @PathVariable String username) {
        UserResponse userResponse = userMapper.userToUserResponse(userService.search(username));
        return ResponseEntity.ok(userResponse);
    }


    /**
     * Получить информацию о своем аккаунте по HttpRequest. Запрос HttpRequest должен содрежать заголовок Authorization
     * @return Структура с детальной информацией о пользователе
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "${api.swagger.user.me}", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> whoami(HttpServletRequest request) {
        UserResponse userResponse = userMapper.userToUserResponse(userService.whoami(request));
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Получить информацию о своем аккаунте по токену авторазиции.
     * @param token Токен авторизпции
     * @return  Структура с детальной информацией о пользователе
     */
    @GetMapping("/me/{token}")
    @ApiOperation(value = "${api.swagger.user.token.me}", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<UserResponse> getUserByToken(@PathVariable String token) {
        UserResponse userResponse = userMapper.userToUserResponse(userService.whoami(token));
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Обновить свою аутентификацию в системе
     * @return Структура со всей информацией о пользователе и токеном аутентификации
     */
    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "${api.swagger.user.refresh}", response = AuthenticationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 404, message = AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String token = userService.refresh(request.getRemoteUser());
        User user = userService.search(request.getRemoteUser());
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return ResponseEntity.ok(new AuthenticationResponse(userResponse, token));
    }


    /**
     * Проверка токена на валидность.
     * Если данный токен был выдан данной системой и он не просрочен, то валидация токена считается успешной
     * @param token Токен выданный при авторазиции
     * @return True -  если данному токену сооствествует успешнаяя авторазиция в данный момент
     */
    @ApiOperation(value = "${api.swagger.user.token.validate}", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 404, message = AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    @GetMapping("/token/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean tokenIsValid = userService.tokeIsValid(token);
        return ResponseEntity.ok(tokenIsValid);
    }

}
