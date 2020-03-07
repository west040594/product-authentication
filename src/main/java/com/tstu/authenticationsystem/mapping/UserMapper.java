package com.tstu.authenticationsystem.mapping;

import com.tstu.authenticationsystem.model.Role;
import com.tstu.authenticationsystem.model.User;
import com.tstu.authenticationsystem.service.RoleService;
import com.tstu.commons.dto.http.request.authenticationsystem.UserDataRequest;
import com.tstu.commons.dto.http.response.authenticationsystem.UserResponse;
import com.tstu.commons.mapping.DateMapper;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {DateMapper.class})
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "firstName", source = "firstName"),
            @Mapping(target = "lastName", source = "lastName"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "createTime", source = "createTime"),
            @Mapping(target = "lastModifyTime", source = "lastModifyTime"),
            @Mapping(target = "roles", ignore = true),
    })
    UserResponse userToUserResponse(User user);


    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "firstName", source = "firstName"),
            @Mapping(target = "lastName", source = "lastName"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "lastModifyTime", ignore = true),
            @Mapping(target = "roles", ignore = true),
    })
    User userDataRequestToUser(UserDataRequest request, @Context RoleService roleService);


    @AfterMapping
    default void afterMappingToUserFromUserDataRequest(UserDataRequest userDataRequest,
                                                       @MappingTarget User user,
                                                       @Context RoleService roleService) {
        if(userDataRequest != null && userDataRequest.getRoles() != null) {
            Set<Role> roles = userDataRequest.getRoles().stream()
                    .map(roleService::search)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
    }

    @AfterMapping
    default void afterMappingToUserResponseFromUser(User user, @MappingTarget UserResponse userResponse) {
        if(user != null) {
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            userResponse.setRoles(roles);
        }
    }
}
