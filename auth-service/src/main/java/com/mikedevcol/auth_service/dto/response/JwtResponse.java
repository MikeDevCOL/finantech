package com.mikedevcol.auth_service.dto.response;

/**
 * JwtResponse
 */
public record JwtResponse(
		long accessTokenExpiration) {
}