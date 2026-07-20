package com.cendekia.user_service.dtos.refresh;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccessTokenRequestDTO {
    @NotBlank(message = "Refresh token is required")
    @NotNull(message = "Refresh token is required")
    @Size(min = 100, message = "The refresh token given is too short")
    private String token;
}
