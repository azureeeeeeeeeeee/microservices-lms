package com.cendekia.user_service.dtos.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @Size(min = 8, max = 255, message = "UID must be at least 8 characters long")
    @NotBlank(message = "UID is required")
    @NotNull(message = "UID is required")
    private String uid;


    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long")
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;
}
