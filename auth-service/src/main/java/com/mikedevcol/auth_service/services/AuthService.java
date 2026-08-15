package com.mikedevcol.auth_service.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mikedevcol.auth_service.configuration.JwtProperties;
import com.mikedevcol.auth_service.dto.response.JwtResponse;
import com.mikedevcol.auth_service.dto.response.LoginResponse;
import com.mikedevcol.auth_service.exception.DataNotFoundException;
import com.mikedevcol.auth_service.exception.InvalidCredentialException;
import com.mikedevcol.auth_service.mappers.UserMapper;
import com.mikedevcol.auth_service.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtils jwtUtils;
  private final UserService userService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public LoginResponse login(String username, String password) throws InvalidCredentialException {

    try {
      var user = userService.findByUsername(username);

      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new InvalidCredentialException("Invalid username or password");
      }

      return new LoginResponse(true, userMapper.toResponse(user));
    } catch (DataNotFoundException _) {
      throw new InvalidCredentialException("Invalid username or password");
    }

  }

  public String generateRefreshToken(String username) {
    var user = userService.findByUsername(username);
    return jwtUtils.generateRefreshToken(user.getUsername());
  }

  public String generateAccessToken(String username) {
    var user = userService.findByUsername(username);
    return jwtUtils.generateAccessToken(user.getUsername(), String.valueOf(user.getRole().getId()));
  }

}
