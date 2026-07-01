package com.cendekia.user_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @Size(min = 8, max = 255, message = "UID must be at least 8 characters long")
    @NotBlank(message = "UID is required")
    @NotNull(message = "UID is required")
    private String uid;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email is required")
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long")
    @NotBlank(message = "Password is required")
    @NotNull(message = "Password is required")
    private String password;

    @NotBlank(message = "Full name is required")
    @NotNull(message = "Full name is required")
    private String fullname;

    @Size(min = 8, max = 100, message = "Confirm password must be between 8 and 100 characters long")
    @NotBlank(message = "Confirm password is required")
    @NotNull(message = "Confirm password is required")
    private String confirmPassword;
}
