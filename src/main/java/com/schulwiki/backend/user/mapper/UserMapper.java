package com.schulwiki.backend.user.mapper;

import com.schulwiki.backend.auth.dto.RegisterRequest;
import com.schulwiki.backend.user.dto.UserResponse;
import com.schulwiki.backend.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse mapToResponse(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    @Mapping(source = "validationCredentialsRequest.username", target = "username")
    @Mapping(source = "validationCredentialsRequest.password", target = "password", ignore = true)
    UserEntity mapToEntity(RegisterRequest request);
}
