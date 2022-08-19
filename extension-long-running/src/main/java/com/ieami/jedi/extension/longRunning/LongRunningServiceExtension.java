package com.ieami.jedi.extension.longRunning;

import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.core.Extension;
import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

public final class LongRunningServiceExtension extends Extension {

    public LongRunningServiceExtension(@NotNull DependencyCollection dependencyCollection) {
        super(dependencyCollection);
    }

    public <S, Impl extends S> @NotNull LongRunningServiceExtension addLongRunningService(
            @NotNull Class<S> serviceClass,
            @NotNull Class<Impl> implementationClass,
            @NotNull ExecutorService executeOn
    ) {
        dependencyCollection.addSingleton(serviceClass, implementationClass);
        return this;
    }

//    public @NotNull LongRunningServiceExtension addBackgroundService() {
//        return addLongRunningService();
//    }
//
//    public @NotNull LongRunningServiceExtension addHostedService() {
//        return addLongRunningService();
//    }

    @Override
    protected void beforeBuild() {

    }

    @Override
    protected void afterBuild(@NotNull DependencyResolver dependencyResolver) {
        super.afterBuild(dependencyResolver);
    }
}

