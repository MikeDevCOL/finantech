package com.mikedevcol.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mikedevcol.auth_service.dto.request.UserRequest;
import com.mikedevcol.auth_service.dto.response.UserResponse;
import com.mikedevcol.auth_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest user) {
    UserResponse registeredUser = userService.registerUser(user);
    return ResponseEntity.ok(registeredUser);
  }
}
