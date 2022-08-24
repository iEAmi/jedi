package com.ieami.jedi.extension.scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

final class ScheduledExecutorServiceFactory {
    ScheduledExecutorService create(Class<?> implementationClass) {
        final var threadName = "scheduled-service-" + implementationClass.getName();
        return Executors.newSingleThreadScheduledExecutor(new NamedDaemonThreadFactory(threadName));
    }
}
