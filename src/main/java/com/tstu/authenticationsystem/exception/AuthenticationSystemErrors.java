package com.tstu.authenticationsystem.exception;

import com.tstu.commons.exception.PrsErrorCode;

import java.util.Arrays;
import java.util.Optional;

public enum AuthenticationSystemErrors implements PrsErrorCode {
    EXPIRED_OR_INVALID_JWT_TOKEN(1, AuthenticationExceptionMessage.EXPIRED_OR_INVALID_JWT_TOKEN_MSG),
    USER_NOT_FOUND(2, AuthenticationExceptionMessage.USER_NOT_FOUND_MSG),
    ROLE_NOT_FOUND(3, AuthenticationExceptionMessage.ROLE_NOT_FOUND_MSG),
    USERNAME_IS_ALREADY_IN_USE(4, AuthenticationExceptionMessage.USERNAME_IS_ALREADY_IN_USE_MSG),
    INVALID_USERNAME_OR_PASSWORD(5, AuthenticationExceptionMessage.INVALID_USERNAME_OR_PASSWORD_MSG),
    USER_WITH_TOKEN_NOT_FOUND(6, AuthenticationExceptionMessage.USER_WITH_TOKEN_NOT_FOUND_MSG),
    JSON_NOT_READABLE(7, AuthenticationExceptionMessage.JSON_NOT_READABLE),
    ACCESS_DENIED(8, AuthenticationExceptionMessage.ACCESS_DENIED_MSG);

    private Integer errorCode;
    private String errorDescription;

    AuthenticationSystemErrors(Integer errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public static Optional<AuthenticationSystemErrors> getByDescription(String errorDescription) {
        return Arrays.stream(values())
                .filter(authenticationSystemErrors -> authenticationSystemErrors.errorDescription.equals(errorDescription))
                .findFirst();
    }
}
