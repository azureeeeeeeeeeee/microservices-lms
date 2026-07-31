package com.cendekia.user_service.dtos.refresh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAccessTokenResponseDTO {
    public String message;
    public String accessToken;
    public String refreshToken;
}
