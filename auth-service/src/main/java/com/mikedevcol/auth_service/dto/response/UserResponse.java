package com.mikedevcol.auth_service.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
		Long id,
		String username,
		String email,
		String roleName,
		LocalDateTime createdAt) {

}
