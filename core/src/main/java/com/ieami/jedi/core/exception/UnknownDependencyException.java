package com.ieami.jedi.core.exception;

public final class UnknownDependencyException extends Exception {

    public UnknownDependencyException(String message) {
        super(message);
    }

    public UnknownDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownDependencyException(Throwable cause) {
        super(cause);
    }

    public UnknownDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
