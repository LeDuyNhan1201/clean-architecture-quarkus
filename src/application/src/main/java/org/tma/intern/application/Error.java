package org.tma.intern.application;

import lombok.Getter;

@Getter
public enum Error {

    // Token Errors
    TOKEN_MISSING("common/token-missing", "Error.TokenMissing"),
    TOKEN_INVALID("common/token-invalid", "Error.TokenInvalid"),

    //Rate Limiting Errors
    TOO_MANY_REQUESTS("common/too-many-requests", "Error.TooManyRequests"),
    RATE_LIMIT_EXCEEDED("common/rate-limit-exceeded", "Error.RateLimitExceeded"),

    RESOURCE_NOT_FOUND("common/resource-not-found", "Error.ResourceNotFound"),
    ACTION_FAILED("common/action-failed", "Action.Fail"),
    INVALID_AUTH_INFO("auth/invalid-auth-info", "Error.InvalidAuthInfo")
    ;

    Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}