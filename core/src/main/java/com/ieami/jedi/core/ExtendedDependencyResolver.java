package com.ieami.jedi.core;

import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface ExtendedDependencyResolver extends DependencyResolver {

    @Override
    default <I, Impl extends I> @NotNull Impl resolveRequired(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        final var instance = resolve(abstractionClass, implementationClass);
        if (instance == null)
            throw new IllegalStateException(abstractionClass.getCanonicalName() + " is not registered as dependency");

        return instance;
    }

    @Override
    default <I, Impl extends I> @NotNull Optional<Impl> resolveOptional(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        final var instance = resolve(abstractionClass, implementationClass);
        return Optional.ofNullable(instance);
    }

    @Override
    default <I> @NotNull I[] resolveAllRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        final var instance = resolveAll(abstractionClass);
        if (instance == null)
            throw new IllegalStateException("nothing registered for " + abstractionClass.getCanonicalName());

        return instance;
    }
}
