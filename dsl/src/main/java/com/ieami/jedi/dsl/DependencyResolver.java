package com.ieami.jedi.dsl;

import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public interface DependencyResolver {

    <I, Impl extends I> @Nullable Impl resolve(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    default <I> @Nullable I resolve(@NotNull Class<I> implementationClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        return resolve(implementationClass, implementationClass);
    }

    <I, Impl extends I> @NotNull Impl resolveRequired(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    default <I> @NotNull I resolveRequired(@NotNull Class<I> implementationClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        return resolveRequired(implementationClass, implementationClass);
    }

    <I, Impl extends I> @NotNull Optional<Impl> resolveOptional(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    default <I> @NotNull Optional<I> resolveOptional(@NotNull Class<I> implementationClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        return resolveOptional(implementationClass, implementationClass);
    }

    <I> @Nullable I[] resolveAll(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    <I> @NotNull I[] resolveAllRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    default <I> @Nullable I[] resolveAllUnsafe(@NotNull Class<I> abstractionClass) {
        try {
            return resolveAll(abstractionClass);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 MoreThanOneDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @Nullable Impl resolveUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return resolve(abstractionClass, implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @Nullable I resolveUnsafe(@NotNull Class<I> implementationClass) {
        try {
            return resolve(implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull Impl resolveRequiredUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return resolveRequired(abstractionClass, implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull I resolveRequiredUnsafe(@NotNull Class<I> implementationClass) {
        try {
            return resolveRequired(implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull Optional<Impl> resolveOptionalUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return resolveOptional(abstractionClass, implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull Optional<I> resolveOptionalUnsafe(@NotNull Class<I> implementationClass) {
        try {
            return resolveOptional(implementationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
