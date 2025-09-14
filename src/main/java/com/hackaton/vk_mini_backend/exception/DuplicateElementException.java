package com.hackaton.vk_mini_backend.exception;

import java.io.Serial;

public class DuplicateElementException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateElementException(final String message) {
        super(message);
    }
}
