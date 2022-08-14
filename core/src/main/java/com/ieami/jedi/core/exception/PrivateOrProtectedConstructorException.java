package com.ieami.jedi.core.exception;

public final class PrivateOrProtectedConstructorException extends Exception {
    public PrivateOrProtectedConstructorException(String message) {
        super(message);
    }

    public PrivateOrProtectedConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrivateOrProtectedConstructorException(Throwable cause) {
        super(cause);
    }

    public PrivateOrProtectedConstructorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
