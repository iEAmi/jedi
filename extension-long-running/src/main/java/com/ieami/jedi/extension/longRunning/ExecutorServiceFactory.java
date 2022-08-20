package com.ieami.jedi.extension.longRunning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ExecutorServiceFactory {

    public ExecutorService create(Class<?> implementationClass) {
        final var threadName = "long-running-" + implementationClass.getName();
        return Executors.newSingleThreadExecutor(new NamedDaemonThreadFactory(threadName));
    }
}
