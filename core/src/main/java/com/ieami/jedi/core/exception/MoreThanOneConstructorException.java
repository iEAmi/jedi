package com.ieami.jedi.core.exception;

public final class MoreThanOneConstructorException extends Exception {
    public MoreThanOneConstructorException(String message) {
        super(message);
    }

    public MoreThanOneConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoreThanOneConstructorException(Throwable cause) {
        super(cause);
    }

    public MoreThanOneConstructorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
