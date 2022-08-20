package com.ieami.jedi.core.exception;

public final class DuplicateDependencyException extends Exception {
    public DuplicateDependencyException(String message) {
        super(message);
    }

    public DuplicateDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateDependencyException(Throwable cause) {
        super(cause);
    }

    public DuplicateDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
