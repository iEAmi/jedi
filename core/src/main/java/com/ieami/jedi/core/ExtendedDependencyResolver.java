package com.ieami.jedi.core;

import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface ExtendedDependencyResolver extends DependencyResolver {

    <I> @Nullable I resolve(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    @Override
    default <I> @NotNull I resolveRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var instance = resolve(abstractionClass);
        if (instance == null)
            throw new IllegalStateException(abstractionClass.getCanonicalName() + " is not registered as dependency");

        return instance;
    }

    @Override
    default <I> @NotNull Optional<I> resolveOptional(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var instance = resolve(abstractionClass);
        return Optional.ofNullable(instance);
    }
}
