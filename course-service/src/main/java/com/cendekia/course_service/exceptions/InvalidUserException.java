package com.cendekia.course_service.exceptions;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String mesesage) {
        super(mesesage);
    }    
}
