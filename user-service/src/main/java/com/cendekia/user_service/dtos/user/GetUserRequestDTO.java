package com.cendekia.user_service.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserRequestDTO {
    @Size(min = 150, message = "Token size is invalid")
    @NotBlank(message = "Token is required")
    @NotNull(message = "Token is required")
    public String token;
}
