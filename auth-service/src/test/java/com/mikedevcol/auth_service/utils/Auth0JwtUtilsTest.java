package com.mikedevcol.auth_service.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mikedevcol.auth_service.configuration.JwtProperties;

@ExtendWith(MockitoExtension.class)
class Auth0JwtUtilsTest {

  @Mock
  JwtProperties jwtProperties;

  @Mock
  JwtProperties.Expiration expiration;

  @InjectMocks
  Auth0JwtUtils sut;

  @Test
  void generateAndValidateAccessToken_andParseClaims() {
    when(jwtProperties.secretKey()).thenReturn("secret-key-1234567890");
    when(jwtProperties.issuer()).thenReturn("test-issuer");
    when(jwtProperties.expiration())
        .thenReturn(new JwtProperties.Expiration(Duration.ofMinutes(5), Duration.ofDays(1)));

    String access = sut.generateAccessToken("user123", "role-admin");
    assertNotNull(access);
    assertTrue(sut.validateToken(access));

    JwtClaims claims = sut.parseToken(access);
    assertEquals("user123", claims.subject());
    assertEquals("role-admin", claims.roleId());
    assertEquals("test-issuer", claims.issuer());
    assertTrue(claims.accessTokenExpiration() > Instant.now().toEpochMilli());
    assertTrue(claims.refreshTokenExpiration() > Instant.now().toEpochMilli());
  }

  @Test
  void generateAndValidateRefreshToken() {
    when(jwtProperties.secretKey()).thenReturn("another-secret");
    when(jwtProperties.issuer()).thenReturn("issuer-2");
    when(jwtProperties.expiration())
        .thenReturn(new JwtProperties.Expiration(Duration.ofMinutes(10), Duration.ofDays(2)));

    String refresh = sut.generateRefreshToken("refresh-subject");
    assertNotNull(refresh);
    assertTrue(sut.validateToken(refresh));

    JwtClaims claims = sut.parseToken(refresh);
    assertEquals("refresh-subject", claims.subject());
    assertNull(claims.roleId());
    assertEquals("issuer-2", claims.issuer());
  }

  @Test
  void validateToken_returnsFalse_forTamperedToken() {
    when(jwtProperties.secretKey()).thenReturn("s3cr3t");
    when(jwtProperties.issuer()).thenReturn("iss");
    when(jwtProperties.expiration())
        .thenReturn(new JwtProperties.Expiration(Duration.ofMinutes(1), Duration.ofHours(1)));

    String token = sut.generateAccessToken("u", "r");
    // tamper the token
    String tampered = token + "x";
    assertFalse(sut.validateToken(tampered));
  }

}
