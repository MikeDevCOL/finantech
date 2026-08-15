package com.mikedevcol.auth_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mikedevcol.auth_service.configuration.JwtProperties;
import com.mikedevcol.auth_service.dto.request.UserRequest;
import com.mikedevcol.auth_service.dto.response.LoginResponse;
import com.mikedevcol.auth_service.dto.response.UserResponse;
import com.mikedevcol.auth_service.services.AuthService;
import com.mikedevcol.auth_service.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;
  private final AuthService authService;
  private final JwtProperties jwtProperties;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest user) {
    UserResponse registeredUser = userService.registerUser(user);
    return ResponseEntity.ok(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody UserRequest user, HttpServletRequest request) {

    var loginResponse = authService.login(user.username(), user.password());

    var refreshToken = authService.generateRefreshToken(user.username());
    var accessToken = authService.generateAccessToken(user.username());

    ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
    ResponseCookie accessTokenCookie = createAccessTokenCookie(accessToken);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
        .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
        .body(loginResponse);
  }

  private ResponseCookie createAccessTokenCookie(String accessToken) {
    return ResponseCookie.from("accessToken", accessToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(jwtProperties.expiration().accessToken().getSeconds())
        .sameSite("Strict")
        .build();
  }

  private ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(jwtProperties.expiration().refreshToken().getSeconds())
        .sameSite("Strict")
        .build();
  }
}
