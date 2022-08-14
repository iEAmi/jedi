package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import com.ieami.jedi.dsl.Abstraction;
import com.ieami.jedi.dsl.Dependency;
import org.jetbrains.annotations.NotNull;

public interface DependencyCollection {

    <I, Impl extends I> @NotNull DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency);

    @NotNull DependencyResolver build() throws MoreThanOneConstructorException, AbstractImplementationException, InterfaceImplementationException, UnknownDependencyException, PrivateOrProtectedConstructorException;

    default <I, Impl extends I> @NotNull DependencyCollection addTransient(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) {
        final var dependency =  Abstraction.abstraction(abstractionClass)
                .implementedBy(implementationClass)
                .asTransient();

        return addDependency(dependency);
    }
}
