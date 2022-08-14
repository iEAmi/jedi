package com.ieami.jedi.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface DependencyResolver {

    <I> @Nullable I resolve(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    default <I> @NotNull I resolveRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var instance = resolve(abstractionClass);
        if (instance == null)
            throw new IllegalStateException(abstractionClass.getCanonicalName() + " is not registered as dependency");

        return instance;
    }

    default <I> @NotNull Optional<I> resolveOptional(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var instance = resolve(abstractionClass);
        return Optional.ofNullable(instance);
    }
}
