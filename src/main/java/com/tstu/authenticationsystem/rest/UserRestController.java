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

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}")
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

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 422, message = AuthenticationExceptionMessage.UNABLE_TO_PROCESS_DATA),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> sigup(@ApiParam("Signup User") @RequestBody UserDataRequest userDataRequest) {
        String token = userService.signUp(userMapper.userDataRequestToUser(userDataRequest, roleService));
        User user = userService.search(userDataRequest.getUsername());
        UserResponse userResponse = userMapper.userToUserResponse(user);
        return ResponseEntity.ok(new AuthenticationResponse(userResponse, token));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.delete}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 404, message = AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> delete(@ApiParam("Username") @PathVariable String username) {
        userService.delete(username);
        return ResponseEntity.ok(username);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.search}", response = UserResponse.class)
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


    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "${UserController.me}", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<?> whoami(HttpServletRequest request) {
        log.info("who am i run");
        UserResponse userResponse = userMapper.userToUserResponse(userService.whoami(request));
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/me/{token}")
    @ApiOperation(value = "${UserController.me}", response = UserResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = AuthenticationExceptionMessage.UNEXPECTED_ERROR_MSG),
            @ApiResponse(code = 403, message = AuthenticationExceptionMessage.ACCESS_DENIED_MSG),
            @ApiResponse(code = 500, message = AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG)
    })
    public ResponseEntity<UserResponse> getUserByToken(@PathVariable String token) {
        UserResponse userResponse = userMapper.userToUserResponse(userService.whoami(token));
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "${UserController.refresh}", response = AuthenticationResponse.class)
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


    @GetMapping("/token/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean tokenIsValid = userService.tokeIsValid(token);
        return ResponseEntity.ok(tokenIsValid);
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello");
    }
}
