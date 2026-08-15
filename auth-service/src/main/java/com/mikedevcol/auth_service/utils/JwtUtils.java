package com.mikedevcol.auth_service.utils;

public interface JwtUtils {

  String generateAccessToken(String subject, String roleId);

  String generateRefreshToken(String subject);

  boolean validateToken(String token);

  JwtClaims parseToken(String token);
}
