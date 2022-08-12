package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Dependency<I, Impl extends I> {
    @NotNull Implementation<I, Impl> implementation();

    default @NotNull Class<I> abstractionClass() {
        return implementation().abstraction().getAbstractionClass();
    }

    default @NotNull Class<Impl> implementationClass() {
        return implementation().implementationClass();
    }

    final class Singleton<I, Impl extends I> implements Dependency<I, Impl> {
        private final @NotNull Implementation<I, Impl> implementation;

        public Singleton(@NotNull Implementation<I, Impl> implementation) {
            this.implementation = Objects.requireNonNull(implementation, "implementation");
        }

        @Override
        public @NotNull Implementation<I, Impl> implementation() {
            return this.implementation;
        }

        @Override
        public String toString() {
            return "Singleton instance of (" + this.implementation + ")";
        }
    }
}
