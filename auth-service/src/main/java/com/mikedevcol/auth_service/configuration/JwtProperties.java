package com.mikedevcol.auth_service.configuration;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String issuer, String secretKey, Expiration expiration) {

  @ConfigurationProperties(prefix = "jwt.expiration")
  public record Expiration(Duration accessToken, Duration refreshToken) {
  }

}
