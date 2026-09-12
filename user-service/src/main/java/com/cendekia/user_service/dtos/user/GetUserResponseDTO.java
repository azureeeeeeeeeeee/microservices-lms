package com.cendekia.user_service.dtos.user;

import java.util.Map;

import com.cendekia.user_service.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponseDTO {
    public String message;
    public String userId;
    public String email;
    public String fullname;
    public Role role;
}
