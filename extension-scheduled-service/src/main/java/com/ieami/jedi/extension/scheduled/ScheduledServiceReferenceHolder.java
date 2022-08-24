package com.ieami.jedi.extension.scheduled;

import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

interface ScheduledServiceReferenceHolder {

    void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException;

    void stop();

    final class Delay implements ScheduledServiceReferenceHolder {
        private final @NotNull ScheduledExecutorService scheduledExecutorService;
        private final @NotNull Class<? extends Runnable> implementationClass;
        private final @NotNull TimeUnit timeUnit;
        private final long delay;

        Delay(
                @NotNull ScheduledExecutorService scheduledExecutorService,
                @NotNull Class<? extends Runnable> implementationClass,
                @NotNull TimeUnit timeUnit,
                long delay
        ) {
            this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
            this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit");
            this.delay = delay;
        }

        @Override
        public void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException {
            final var runnable = dependencyResolver.resolveRequired(implementationClass);

            this.scheduledExecutorService.schedule(runnable, delay, timeUnit);
        }

        @Override
        public void stop() {
            scheduledExecutorService.shutdown();
        }
    }

    final class FixedRate implements ScheduledServiceReferenceHolder {
        private final @NotNull ScheduledExecutorService scheduledExecutorService;
        private final @NotNull Class<? extends Runnable> implementationClass;
        private final @NotNull TimeUnit timeUnit;
        private final long initialDelay;
        private final long period;

        FixedRate(
                @NotNull ScheduledExecutorService scheduledExecutorService,
                @NotNull Class<? extends Runnable> implementationClass,
                @NotNull TimeUnit timeUnit,
                long initialDelay,
                long period
        ) {
            this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
            this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit");
            this.initialDelay = initialDelay;
            this.period = period;
        }

        @Override
        public void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException {
            final var runnable = dependencyResolver.resolveRequired(implementationClass);

            this.scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
        }

        @Override
        public void stop() {
            scheduledExecutorService.shutdown();
        }
    }

    final class FixedDelay implements ScheduledServiceReferenceHolder {
        private final @NotNull ScheduledExecutorService scheduledExecutorService;
        private final @NotNull Class<? extends Runnable> implementationClass;
        private final @NotNull TimeUnit timeUnit;
        private final long initialDelay;
        private final long delay;

        FixedDelay(
                @NotNull ScheduledExecutorService scheduledExecutorService,
                @NotNull Class<? extends Runnable> implementationClass,
                @NotNull TimeUnit timeUnit,
                long initialDelay,
                long delay
        ) {
            this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
            this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit");
            this.initialDelay = initialDelay;
            this.delay = delay;
        }

        @Override
        public void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException {
            final var runnable = dependencyResolver.resolveRequired(implementationClass);

            this.scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
        }

        @Override
        public void stop() {
            scheduledExecutorService.shutdown();
        }
    }
}
