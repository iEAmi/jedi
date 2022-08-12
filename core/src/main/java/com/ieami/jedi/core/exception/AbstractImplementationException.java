package com.ieami.jedi.core.exception;

public final class AbstractImplementationException extends Exception {

    public AbstractImplementationException(String message) {
        super(message);
    }

    public AbstractImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractImplementationException(Throwable cause) {
        super(cause);
    }

    public AbstractImplementationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
