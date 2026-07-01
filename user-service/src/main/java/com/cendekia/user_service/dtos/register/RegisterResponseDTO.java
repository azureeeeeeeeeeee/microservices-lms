package com.cendekia.user_service.dtos.register;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDTO {
    private String message;
    private Map<String, String> data;
}
