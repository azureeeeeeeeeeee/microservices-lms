package com.cendekia.user_service.dtos.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String message;
    private String token;
}
