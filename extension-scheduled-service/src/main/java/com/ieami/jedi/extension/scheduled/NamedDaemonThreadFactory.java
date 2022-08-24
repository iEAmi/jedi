package com.ieami.jedi.extension.scheduled;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;

final class NamedDaemonThreadFactory implements ThreadFactory {
    private final @NotNull String name;

    NamedDaemonThreadFactory(@NotNull String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        final var thread = new Thread(r);
        thread.setName(name);
        thread.setDaemon(true);
        return thread;
    }
}
