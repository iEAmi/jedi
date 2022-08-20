package com.ieami.jedi.extension.longRunning;

import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.core.Extension;
import com.ieami.jedi.core.exception.DuplicateDependencyException;
import com.ieami.jedi.core.exception.ExtensionException;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

public final class LongRunningServiceExtension extends Extension implements Runnable {
    private final @NotNull ExecutorServiceFactory executorServiceFactory = new ExecutorServiceFactory();
    private LongRunningServiceReferenceHolder[] longRunningServiceReferenceHolders;

    public LongRunningServiceExtension(@NotNull DependencyCollection dependencyCollection) {
        super(dependencyCollection);
    }

    public <S extends Runnable, Impl extends S> @NotNull LongRunningServiceExtension addLongRunningService(
            @NotNull Class<Impl> implementationClass,
            @NotNull ExecutorService executeOn
    ) throws DuplicateDependencyException {
        final var referenceHolder = new LongRunningServiceReferenceHolder.Default(executeOn, implementationClass);
        dependencyCollection.addSingleton(Runnable.class, implementationClass);
        dependencyCollection.addSingleton(LongRunningServiceReferenceHolder.class, referenceHolder);
        return this;
    }

    public <S extends Runnable, Impl extends S> @NotNull LongRunningServiceExtension addLongRunningServiceUnsafe(
            @NotNull Class<Impl> implementationClass,
            @NotNull ExecutorService executeOn
    ) {
        try {
            return addLongRunningService(implementationClass, executeOn);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    public <S extends Runnable, Impl extends S> @NotNull LongRunningServiceExtension addLongRunningService(
            @NotNull Class<Impl> implementationClass
    ) throws DuplicateDependencyException {
        final var executorService = executorServiceFactory.create(implementationClass);
        return addLongRunningService(implementationClass, executorService);
    }

    public <S extends Runnable, Impl extends S> @NotNull LongRunningServiceExtension addLongRunningServiceUnsafe(
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return addLongRunningService(implementationClass);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void beforeBuild() throws ExtensionException {
        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    @Override
    protected void afterBuild(@NotNull DependencyResolver dependencyResolver) throws ExtensionException {
        try {
            this.longRunningServiceReferenceHolders = dependencyResolver.resolveAllRequired(LongRunningServiceReferenceHolder.class);
            for (final var longRunningServiceReferenceHolder : longRunningServiceReferenceHolders) {
                longRunningServiceReferenceHolder.start(dependencyResolver);
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                 MoreThanOneDependencyException e) {
            throw new ExtensionException(e);
        }
    }

    @Override
    public void run() {
        if (this.longRunningServiceReferenceHolders == null)
            return;

        for (final var longRunningServiceReferenceHolder : longRunningServiceReferenceHolders) {
            longRunningServiceReferenceHolder.stop();
        }
    }
}

