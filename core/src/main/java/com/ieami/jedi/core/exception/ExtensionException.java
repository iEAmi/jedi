package com.ieami.jedi.core.exception;

public final class ExtensionException extends Exception {
    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionException(Throwable cause) {
        super(cause);
    }

    public ExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
