package com.tstu.authenticationsystem.service;

import com.tstu.authenticationsystem.model.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    String signIn(String username, String password);
    String signUp(User user);
    void delete(String username);
    User search(String username);
    User whoami(HttpServletRequest request);
    User whoami(String token);
    String refresh(String username);
    boolean tokeIsValid(String token);
}
