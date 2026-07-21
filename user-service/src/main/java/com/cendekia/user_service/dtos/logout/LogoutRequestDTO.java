package com.cendekia.user_service.dtos.logout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    @NotBlank(message = "Token is required")
    @NotNull(message = "Token is required")
    public String token;
}
