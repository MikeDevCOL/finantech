package com.mikedevcol.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record UserRequest(
    @NotBlank @Size(min = 5, max = 50) String username,
    @NotBlank @Size(min = 12, max = 100) String password,
    @NotBlank @Email @Size(max = 100) String email,
    Long roleId) {

}
