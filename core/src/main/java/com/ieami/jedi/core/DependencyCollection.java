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

    <I, Impl extends I> @NotNull DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency);

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

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .implementedBy(implementationClass)
                .asTransient();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) {
        final var dependency = Abstraction.abstraction(abstractionClass)
                .instantiateUsing(instantiator)
                .asTransient();

        return addDependency(dependency);
    }
}
