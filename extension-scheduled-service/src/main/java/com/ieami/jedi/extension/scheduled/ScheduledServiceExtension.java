package com.ieami.jedi.extension.scheduled;

import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.core.Extension;
import com.ieami.jedi.core.exception.ExtensionException;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public final class ScheduledServiceExtension extends Extension {
    private ScheduledServiceReferenceHolder[] scheduledServiceReferenceHolders;

    public ScheduledServiceExtension(@NotNull DependencyCollection dependencyCollection) {
        super(dependencyCollection);
    }


    public <I extends Runnable> @NotNull ScheduleDefinition schedule(@NotNull Class<I> implementationClass) {
        return new ScheduleDefinition(dependencyCollection, implementationClass);
    }

    @Override
    protected void beforeBuild() throws ExtensionException {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    protected void afterBuild(@NotNull DependencyResolver dependencyResolver) throws ExtensionException {
        try {
            this.scheduledServiceReferenceHolders = dependencyResolver.resolveAllRequired(ScheduledServiceReferenceHolder.class);
            for (final var scheduledServiceReferenceHolder : scheduledServiceReferenceHolders) {
                scheduledServiceReferenceHolder.start(dependencyResolver);
            }
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                 MoreThanOneDependencyException e) {
            throw new ExtensionException(e);
        }
    }

    private void close() {
        if (this.scheduledServiceReferenceHolders == null)
            return;

        for (final var longRunningServiceReferenceHolder : scheduledServiceReferenceHolders) {
            longRunningServiceReferenceHolder.stop();
        }
    }
}
