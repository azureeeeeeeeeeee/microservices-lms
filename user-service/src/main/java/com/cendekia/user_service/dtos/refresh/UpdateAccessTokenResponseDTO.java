package com.cendekia.user_service.dtos.refresh;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAccessTokenResponseDTO {
    public String message;
    public Map<String, String> data;
}
