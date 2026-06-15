package com.cendekia.user_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String uid;
    private String password;
}
