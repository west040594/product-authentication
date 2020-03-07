package com.tstu.authenticationsystem.exception;

import com.tstu.commons.exception.ExceptionMessage;

public interface AuthenticationExceptionMessage  extends ExceptionMessage {
    String USER_NOT_FOUND_MSG = "Пользователь не найден";
    String USER_WITH_TOKEN_NOT_FOUND_MSG = "Пользователь с таким токеном не найден";
    String ROLE_NOT_FOUND_MSG = "Роль не найдена";
    String USERNAME_IS_ALREADY_IN_USE_MSG = "Пользователь с таким именем уже аутентифицирован";
    String INVALID_USERNAME_OR_PASSWORD_MSG = "Неправильное имя или пароль";
}
