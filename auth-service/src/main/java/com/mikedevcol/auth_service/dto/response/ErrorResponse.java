package com.mikedevcol.auth_service.dto.response;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record ErrorResponse(
    String message,
    String details,
    String path,
    String timestamp) {

}
