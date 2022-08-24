package com.ieami.jedi.extension.config.exception;

public final class ConfigFileNotFoundException extends Exception {
    public ConfigFileNotFoundException(String message) {
        super(message);
    }

    public ConfigFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
