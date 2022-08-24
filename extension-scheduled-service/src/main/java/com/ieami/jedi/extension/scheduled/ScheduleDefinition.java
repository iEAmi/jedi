package com.ieami.jedi.extension.scheduled;

import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.core.exception.DuplicateDependencyException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class ScheduleDefinition {
    private final @NotNull DependencyCollection dependencyCollection;
    private final @NotNull ScheduledExecutorServiceFactory scheduledExecutorServiceFactory = new ScheduledExecutorServiceFactory();
    private final @NotNull Class<? extends Runnable> implementationClass;

    ScheduleDefinition(
            @NotNull DependencyCollection dependencyCollection,
            @NotNull Class<? extends Runnable> implementationClass
    ) {
        this.dependencyCollection = Objects.requireNonNull(dependencyCollection, "dependencyCollection");
        this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
    }

    public void afterDelay(long delay, @NotNull TimeUnit timeUnit) throws DuplicateDependencyException {
        final var executeOn = scheduledExecutorServiceFactory.create(implementationClass);
        final var referenceHolder = new ScheduledServiceReferenceHolder.Delay(executeOn, implementationClass, timeUnit, delay);

        dependencyCollection.addSingleton(implementationClass);
        dependencyCollection.addSingleton(ScheduledServiceReferenceHolder.class, referenceHolder);
    }

    public void afterDelayUnsafe(long delay, @NotNull TimeUnit timeUnit) {
        try {
            afterDelay(delay, timeUnit);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    public void atFixedRate(long initialDelay, long period, @NotNull TimeUnit timeUnit) throws DuplicateDependencyException {
        final var executeOn = scheduledExecutorServiceFactory.create(implementationClass);
        final var referenceHolder = new ScheduledServiceReferenceHolder.FixedRate(executeOn, implementationClass, timeUnit, initialDelay, period);

        dependencyCollection.addSingleton(implementationClass);
        dependencyCollection.addSingleton(ScheduledServiceReferenceHolder.class, referenceHolder);
    }

    public void atFixedRateUnsafe(long initialDelay, long period, @NotNull TimeUnit timeUnit) {
        try {
            atFixedRate(initialDelay, period, timeUnit);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    public void withFixedDelay(long initialDelay, long delay, @NotNull TimeUnit timeUnit) throws DuplicateDependencyException {
        final var executeOn = scheduledExecutorServiceFactory.create(implementationClass);
        final var referenceHolder = new ScheduledServiceReferenceHolder.FixedDelay(executeOn, implementationClass, timeUnit, initialDelay, delay);

        dependencyCollection.addSingleton(implementationClass);
        dependencyCollection.addSingleton(ScheduledServiceReferenceHolder.class, referenceHolder);
    }

    public void withFixedDelayUnsafe(long initialDelay, long delay, @NotNull TimeUnit timeUnit) {
        try {
            withFixedDelay(initialDelay, delay, timeUnit);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }
}
