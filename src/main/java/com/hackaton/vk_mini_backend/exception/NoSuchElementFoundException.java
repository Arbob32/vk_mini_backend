package com.hackaton.vk_mini_backend.exception;

import java.io.Serial;

public class NoSuchElementFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoSuchElementFoundException(final String message) {
        super(message);
    }
}
