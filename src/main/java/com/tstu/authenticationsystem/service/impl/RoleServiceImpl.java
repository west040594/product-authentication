package com.tstu.authenticationsystem.service.impl;

import com.tstu.authenticationsystem.exception.AuthenticationSystemErrors;
import com.tstu.authenticationsystem.model.Role;
import com.tstu.authenticationsystem.repository.RoleRepository;
import com.tstu.authenticationsystem.service.RoleService;
import com.tstu.commons.exception.PrsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role search(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new PrsHttpException(AuthenticationSystemErrors.ROLE_NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY));
    }
}
