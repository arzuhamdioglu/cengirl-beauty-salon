package com.cengirl.beautysalon.domain.exception;

/**
 * Base exception for predictable business-rule violations.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
