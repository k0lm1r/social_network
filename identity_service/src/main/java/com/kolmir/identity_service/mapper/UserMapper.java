package com.kolmir.identity_service.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.identity_service.dto.UserCreateRequest;
import com.kolmir.identity_service.dto.UserRegisterRequest;
import com.kolmir.identity_service.dto.UserResponse;
import com.kolmir.identity_service.dto.UserUpdateRequest;
import com.kolmir.identity_service.model.User;
import com.kolmir.identity_service.model.UserRole;
import com.kolmir.identity_service.util.AuthUtils;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {
        UserRole.class,
        AuthUtils.class
    }
)
public interface UserMapper {
    public UserResponse toUserResponse(User user);
    public com.kolmir.validate_token.UserResponse toGrpcUserResponse(UserResponse user);
    public User toUser(UserUpdateRequest request);

    @Mapping(target = "role", constant = "USER")
    public User toUser(UserRegisterRequest request);

    public User toUser(UserCreateRequest request);

    @Mapping(target = "password", expression = "java(AuthUtils.generatePassword(8))")
    public UserRegisterRequest toUserRegisterRequest(UserCreateRequest request);

    public List<UserResponse> toResponses(List<User> users);

    @AfterMapping
    public default void setRegistrationTime(UserRegisterRequest request, @MappingTarget User user) {
        user.setRegisteredAt(LocalDateTime.now());
        user.setIsEnabled(true);
    }
}
