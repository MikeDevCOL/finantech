package com.mikedevcol.auth_service.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoggerConfiguration {
  private static final Logger log = LoggerFactory.getLogger(LoggerConfiguration.class);

  private final JwtProperties jwtProperties;

  @PostConstruct
  public void logConfiguration() {
    log.info("JWT expiration: {}", jwtProperties.expiration());
    log.info("JWT issuer: {}", jwtProperties.issuer());
  }
}
