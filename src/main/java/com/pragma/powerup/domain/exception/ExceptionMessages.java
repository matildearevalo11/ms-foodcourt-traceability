package com.pragma.powerup.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionMessages {
    INVALID_INTERNAL_API_KEY("Invalid internal API key"),
    TRACEABILITY_PERSISTENCE_ERROR("Traceability could not be accessed"),
    AUTHENTICATED_USER_NOT_FOUND("Authenticated user not found"),
    INVALID_AUTHENTICATED_USER_ID("Authenticated user ID is invalid"),
    ACCESS_DENIED("Access denied");

    private final String message;
}
