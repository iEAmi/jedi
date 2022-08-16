package com.ieami.jedi.extension.config.json;

import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

final class ConfigHolderFunction<I> implements Function<@NotNull DependencyResolver, I> {
    private final @NotNull I config;

    ConfigHolderFunction(@NotNull I config) {
        this.config = Objects.requireNonNull(config, "config");
    }


    @Override
    public I apply(@NotNull DependencyResolver dependencyResolver) {
        return config;
    }
}
