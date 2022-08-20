package com.ieami.jedi.dsl.exception;

public final class MoreThanOneDependencyException extends Exception {
    public MoreThanOneDependencyException(String message) {
        super(message);
    }

    public MoreThanOneDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoreThanOneDependencyException(Throwable cause) {
        super(cause);
    }

    public MoreThanOneDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
