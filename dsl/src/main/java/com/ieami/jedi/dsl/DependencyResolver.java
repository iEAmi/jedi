package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface DependencyResolver {

    <I> @Nullable I resolve(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    <I> @NotNull I resolveRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    <I> @NotNull Optional<I> resolveOptional(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    default <I> @Nullable I resolveUnsafe(@NotNull Class<I> abstractionClass) {
        try {
            return resolve(abstractionClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull I resolveRequiredUnsafe(@NotNull Class<I> abstractionClass) {
        try {
            return resolveRequired(abstractionClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull Optional<I> resolveOptionalUnsafe(@NotNull Class<I> abstractionClass) {
        try {
            return resolveOptional(abstractionClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
