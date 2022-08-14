package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Decoration<D extends I, I, Impl extends I> extends Implementation<I, Impl> {
    @NotNull Class<D> decoratorClass();

    @NotNull Implementation<I, Impl> implementation();

    @Override
    default @NotNull Class<Impl> implementationClass() {
        return implementation().implementationClass();
    }

    @Override
    default @NotNull Abstraction<I> abstraction() {
        return implementation().abstraction();
    }

    final class Default<D extends I, I, Impl extends I> implements Decoration<D, I, Impl> {
        private final @NotNull Class<D> decoratorClass;
        private final @NotNull Implementation<I, Impl> implementation;

        Default(@NotNull Class<D> decoratorClass, @NotNull Implementation<I, Impl> implementation) {
            this.decoratorClass = Objects.requireNonNull(decoratorClass, "decoratorClass");
            this.implementation = Objects.requireNonNull(implementation, "implementation");
        }

        @Override
        public @NotNull Class<D> decoratorClass() {
            return this.decoratorClass;
        }

        @Override
        public @NotNull Implementation<I, Impl> implementation() {
            return this.implementation;
        }
    }
}
