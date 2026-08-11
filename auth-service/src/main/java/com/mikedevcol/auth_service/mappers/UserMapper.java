package com.mikedevcol.auth_service.mappers;

import org.springframework.stereotype.Component;

import com.mikedevcol.auth_service.dto.request.UserRequest;
import com.mikedevcol.auth_service.dto.response.UserResponse;
import com.mikedevcol.auth_service.models.User;

@Component
public class UserMapper {

  public UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getRole().getName(),
        user.getCreatedAt());
  }

  public User toEntity(UserRequest userRequest) {
    User user = new User();
    user.setUsername(userRequest.username());
    user.setEmail(userRequest.email());
    user.setPassword(userRequest.password());
    return user;
  }

}
