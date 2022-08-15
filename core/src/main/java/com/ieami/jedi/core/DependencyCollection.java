package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import com.ieami.jedi.dsl.Abstraction;
import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface DependencyCollection {

    static @NotNull DependencyCollection newDefault() {
        return new MapBasedDependencyCollection();
    }

    <I, Impl extends I> @NotNull DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency);

    @NotNull ExtendedDependencyResolver build() throws MoreThanOneConstructorException, AbstractImplementationException, InterfaceImplementationException, UnknownDependencyException, PrivateOrProtectedConstructorException;

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        final var dependency =  Abstraction.abstraction(abstractionClass)
                .implementedBy(implementationClass)
                .asTransient();

        return addDependency(dependency);
    }

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Function<DependencyResolver, Impl> instantiator
            ) {
        final var dependency =  Abstraction.abstraction(abstractionClass)
                .instantiateUsing(instantiator)
                .asTransient();

        return addDependency(dependency);
    }
}
