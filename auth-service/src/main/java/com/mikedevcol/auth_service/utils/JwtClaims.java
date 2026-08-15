package com.mikedevcol.auth_service.utils;

public record JwtClaims(
    String subject,
    String roleId,
    String issuer,
    long refreshTokenExpiration,
    long accessTokenExpiration) {
}