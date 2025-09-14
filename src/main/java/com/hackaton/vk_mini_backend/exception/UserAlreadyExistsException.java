package com.hackaton.vk_mini_backend.exception;

import java.io.Serial;

public class UserAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(final String message) {
        super(message);
    }
}
