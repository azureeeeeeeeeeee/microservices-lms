package com.cendekia.user_service.dtos.user;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    public String message;
    public Map<String, String> data;
}
