package com.cendekia.user_service.dtos.user;

import com.cendekia.user_service.enums.Role;

import jakarta.validation.constraints.Email;        
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {
    @NotBlank(message = "Fullname is required")
    @NotNull(message = "Fullname is required")
    @Size(min = 3, max = 50, message = "Fullname must be between 3 and 50 characters")
    private String fullname;

    @NotBlank(message = "Email is required")
    @NotNull(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotNull(message = "Role is required")
    private Role role;
}
