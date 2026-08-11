package com.mikedevcol.auth_service.services;

import com.mikedevcol.auth_service.dto.request.UserRequest;
import com.mikedevcol.auth_service.mappers.UserMapper;
import com.mikedevcol.auth_service.dto.response.UserResponse;
import com.mikedevcol.auth_service.exception.DataConflictException;
import com.mikedevcol.auth_service.exception.DataNotFoundException;
import com.mikedevcol.auth_service.models.User;
import com.mikedevcol.auth_service.repository.RoleRepository;
import com.mikedevcol.auth_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse registerUser(UserRequest userRequest) throws DataConflictException, DataNotFoundException {

    User user = userMapper.toEntity(userRequest);

    // Check if the username already exists
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      throw new DataConflictException("Username already exists: " + user.getUsername());
    }

    // Only the user role is allowed to be assigned during registration
    // Admin role assignment should be handled separately by an admin user

    user.setRole(roleRepository.findByName("USER")
        .orElseThrow(() -> new DataNotFoundException("Default role not found")));

    // Encode the user's password
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    var userSaved = userRepository.save(user);

    return userMapper.toResponse(userSaved);
  }

}
