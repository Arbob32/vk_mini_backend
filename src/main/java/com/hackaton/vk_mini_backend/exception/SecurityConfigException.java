package com.hackaton.vk_mini_backend.exception;

import java.io.Serial;

public class SecurityConfigException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SecurityConfigException(final String message) {
        super(message);
    }

    public SecurityConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
