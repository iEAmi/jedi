package com.ieami.jedi.core.exception;

public final class InterfaceImplementationException extends Exception {

    public InterfaceImplementationException(String message) {
        super(message);
    }

    public InterfaceImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterfaceImplementationException(Throwable cause) {
        super(cause);
    }

    public InterfaceImplementationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
