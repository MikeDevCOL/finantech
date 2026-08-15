package com.mikedevcol.auth_service.dto.response;

/**
 * LoginResponse
 */
public record LoginResponse(
		boolean success,
		UserResponse user) {
}