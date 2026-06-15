package com.cendekia.user_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String uid;
    private String email;
    private String password;
    private String fullname;
    private String confirmPassword;
}
