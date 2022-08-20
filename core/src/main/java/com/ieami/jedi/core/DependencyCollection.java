package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import com.ieami.jedi.dsl.Abstraction;
import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface DependencyCollection {

    static @NotNull DependencyCollection newDefault() {
        return new MapBasedDependencyCollection();
    }

    <I, Impl extends I> @NotNull DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency) throws DuplicateDependencyException;

    <E extends Extension> @NotNull DependencyCollection extendWith(@NotNull E extension);

    <E extends Extension> @NotNull DependencyCollection extendWith(
            @NotNull E extension,
            @NotNull Consumer<@NotNull E> builder
    );

    <E extends Extension> @NotNull DependencyCollection extendWith(
            @NotNull Function<@NotNull DependencyCollection, @NotNull E> f
    );

    <E extends Extension> @NotNull DependencyCollection extendWith(
            @NotNull Function<@NotNull DependencyCollection, @NotNull E> f,
            @NotNull Consumer<@NotNull E> builder
    );


    @NotNull DependencyResolver build() throws MoreThanOneConstructorException, AbstractImplementationException, InterfaceImplementationException, UnknownDependencyException, PrivateOrProtectedConstructorException;

    default @NotNull DependencyResolver buildUnsafe() {
        try {
            return build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .implementedBy(implementationClass)
                .asTransient();

        return addDependency(dependency);
    }

    default <I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .implementedBy(abstractionClass)
                .asTransient();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass,
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .instantiateUsing(implementationClass, instantiator)
                .asTransient();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Impl instance
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .withInstance(instance)
                .asTransient();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingleton(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .implementedBy(implementationClass)
                .asSingleton();

        return addDependency(dependency);
    }

    default <I> @NotNull DependencyCollection addSingleton(
            @NotNull Class<I> abstractionClass
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .implementedBy(abstractionClass)
                .asSingleton();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingleton(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass,
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .instantiateUsing(implementationClass, instantiator)
                .asSingleton();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingleton(
            @NotNull Class<I> abstractionClass,
            @NotNull Impl instance
    ) throws DuplicateDependencyException {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .withInstance(instance)
                .asSingleton();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingletonUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return addSingleton(abstractionClass, implementationClass);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull DependencyCollection addSingletonUnsafe(
            @NotNull Class<I> abstractionClass
    ) {
        try {
            return addSingleton(abstractionClass);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingletonUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass,
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) {
        try {
            return addSingleton(abstractionClass, implementationClass, instantiator);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addSingletonUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Impl instance
    ) {
        try {
            return addSingleton(abstractionClass, instance);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransientUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        try {
            return addTransient(abstractionClass, implementationClass);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I> @NotNull DependencyCollection addTransientUnsafe(
            @NotNull Class<I> abstractionClass
    ) {
        try {
            return addTransient(abstractionClass);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransientUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass,
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) {
        try {
            return addTransient(abstractionClass, implementationClass, instantiator);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransientUnsafe(
            @NotNull Class<I> abstractionClass,
            @NotNull Impl instance
    ) {
        try {
            return addTransient(abstractionClass, instance);
        } catch (DuplicateDependencyException e) {
            throw new RuntimeException(e);
        }
    }
}
