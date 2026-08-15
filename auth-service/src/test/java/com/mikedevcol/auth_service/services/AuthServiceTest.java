package com.mikedevcol.auth_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mikedevcol.auth_service.dto.response.LoginResponse;
import com.mikedevcol.auth_service.exception.InvalidCredentialException;
import com.mikedevcol.auth_service.mappers.UserMapper;
import com.mikedevcol.auth_service.models.Role;
import com.mikedevcol.auth_service.models.User;
import com.mikedevcol.auth_service.utils.JwtUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  JwtUtils jwtUtils;

  @Mock
  UserService userService;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  UserMapper userMapper;

  @InjectMocks
  AuthService sut;

  @Test
  void loginSuccessReturnsLoginResponseWithJwt() {
    var user = User.builder().id(1L).username("john").password("encoded").email("a@b.com")
        .role(Role.builder().id(2L).name("ADMIN").build())
        .build();
    when(userService.findByUsername("john")).thenReturn(user);
    when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);

    LoginResponse resp = sut.login("john", "plain");

    assertNotNull(resp);
  }

  @Test
  void loginWithWrongPasswordThrowsInvalidCredential() {
    var user = User.builder().id(1L).username("john").password("encoded").email("a@b.com")
        .role(Role.builder().id(2L).name("ADMIN").build())
        .build();
    when(userService.findByUsername("john")).thenReturn(user);
    when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

    assertThrows(InvalidCredentialException.class, () -> sut.login("john", "wrong"));
  }

}
