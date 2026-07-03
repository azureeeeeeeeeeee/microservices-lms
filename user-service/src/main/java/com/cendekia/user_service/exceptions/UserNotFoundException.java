package com.cendekia.user_service.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String meessage) {
        super(meessage);
    }
}
