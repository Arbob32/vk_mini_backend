package com.hackaton.vk_mini_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorResponse extends MessageResponse {

    private String stackTrace;
    private List<ValidationError> errors;
    private HttpStatus status;

    public ErrorResponse() {
        super("");
    }

    public ErrorResponse(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public ErrorResponse(final HttpStatus status, final String message, final Exception ex) {
        super(message);
        this.status = status;
    }

    private record ValidationError(String field, @Nullable String message) {}

    public void addValidationError(final String field, @Nullable final String message) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }

    public int getStatusCode() {
        return status != null ? status.value() : 0;
    }
}
