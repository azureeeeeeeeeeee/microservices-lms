package com.cendekia.user_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @Size(min = 8, max = 255, message = "UID must be at least 8 characters long")
    private String uid;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters long")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullname;

    @Size(min = 8, max = 100, message = "Confirm password must be between 8 and 100 characters long")
    private String confirmPassword;
}
