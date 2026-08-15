package com.mikedevcol.auth_service.utils;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mikedevcol.auth_service.configuration.JwtProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0JwtUtils implements JwtUtils {

  private final JwtProperties jwtProperties;

  @Override
  public String generateAccessToken(String subject, String roleId) {
    Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secretKey());
    return JWT.create()
        .withSubject(subject)
        .withClaim("roleId", roleId)
        .withIssuer(jwtProperties.issuer())
        .withExpiresAt(Date.from(Instant.now().plus(jwtProperties.expiration().accessToken())))
        .sign(algorithm);
  }

  @Override
  public String generateRefreshToken(String subject) {
    Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secretKey());
    return JWT.create()
        .withSubject(subject)
        .withIssuer(jwtProperties.issuer())
        .withExpiresAt(Date.from(Instant.now().plus(jwtProperties.expiration().refreshToken())))
        .sign(algorithm);
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secretKey());
      JWT.require(algorithm).build().verify(token);
      return true;
    } catch (JWTVerificationException _) {
      return false;
    }
  }

  @Override
  public JwtClaims parseToken(String token) {

    Algorithm algorithm = Algorithm.HMAC256(jwtProperties.secretKey());
    var decodedJWT = JWT.require(algorithm).build().verify(token);
    return new JwtClaims(
        decodedJWT.getSubject(),
        decodedJWT.getClaim("roleId").asString(),
        decodedJWT.getIssuer(),
        decodedJWT.getExpiresAt().getTime(),
        decodedJWT.getExpiresAt().getTime());

  }

}
