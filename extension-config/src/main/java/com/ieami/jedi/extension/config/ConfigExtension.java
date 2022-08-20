package com.ieami.jedi.extension.config;

import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.core.Extension;
import com.ieami.jedi.core.exception.DuplicateDependencyException;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.extension.config.exception.DeserializationException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public abstract class ConfigExtension<E extends ConfigExtension<E>> extends Extension {
    protected final @NotNull String configFileName;

    public ConfigExtension(@NotNull DependencyCollection dependencyCollection, @NotNull String configFileName) {
        super(dependencyCollection);
        this.configFileName = Objects.requireNonNull(configFileName, "configFileName");
    }

    public <I> @NotNull E addConfig(@NotNull Class<I> bindToClass, @NotNull String key) throws NoSuchFieldException, DeserializationException, DuplicateDependencyException {
        final var builderFunction = createBuilderFunction(bindToClass, key);

        // TODO: Consider using addSingleton instead of addTransient.
        dependencyCollection.addSingleton(bindToClass, bindToClass, builderFunction);

        return self();
    }

    public <I> @NotNull E addConfigUnsafe(@NotNull Class<I> bindToClass, @NotNull String key) {
        try {
            return addConfig(bindToClass, key);
        } catch (NoSuchFieldException | DeserializationException | DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract <I> @NotNull Function<DependencyResolver, I> createBuilderFunction(
            @NotNull Class<I> bindToClass,
            @NotNull String key
    ) throws NoSuchFieldException, DeserializationException;

    protected abstract @NotNull E self();
}
