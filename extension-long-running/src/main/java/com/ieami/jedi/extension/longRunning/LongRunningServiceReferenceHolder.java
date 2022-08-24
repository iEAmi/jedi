package com.ieami.jedi.extension.longRunning;

import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

interface LongRunningServiceReferenceHolder {

    void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException;

    void stop();

    final class Default implements LongRunningServiceReferenceHolder {
        private final @NotNull ExecutorService executorService;
        private final @NotNull Class<? extends Runnable> implementationClass;

        Default(
                @NotNull ExecutorService executorService,
                @NotNull Class<? extends Runnable> implementationClass
        ) {
            this.executorService = Objects.requireNonNull(executorService, "executorService");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
        }

        @Override
        public void start(@NotNull DependencyResolver dependencyResolver) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException {
            final var runnable = dependencyResolver.resolveRequired(implementationClass);

            this.executorService.execute(runnable);
        }

        @Override
        public void stop() {
            executorService.shutdown();
        }
    }
}
