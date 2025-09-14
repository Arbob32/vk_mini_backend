package com.hackaton.vk_mini_backend.exception;

import java.io.Serial;

public class ForbiddenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenException(final String message) {
        super(message);
    }
}
