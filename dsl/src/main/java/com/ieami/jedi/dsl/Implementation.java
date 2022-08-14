package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Implementation<I, Impl extends I> {
    @NotNull Class<Impl> implementationClass();

    @NotNull Abstraction<I> abstraction();

    default <D extends I> @NotNull Decoration<D, I, Impl> decorateWith(@NotNull Class<D> decoratorClass) {
        return new Decoration.Default<>(decoratorClass, this);
    }

    default @NotNull Dependency<I, Impl> asSingleton() {
        return new Dependency.Singleton<>(this);
    }

    default @NotNull Dependency<I, Impl> asTransient() {
        return new Dependency.Transient<>(this);
    }

    final class Default<I, Impl extends I> implements Implementation<I, Impl> {
        private final @NotNull Class<Impl> implementationClass;
        private final @NotNull Abstraction<I> abstraction;

        Default(@NotNull Class<Impl> implementationClass, @NotNull Abstraction<I> abstraction) {
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
            this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
        }

        @Override
        public @NotNull Class<Impl> implementationClass() {
            return this.implementationClass;
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.abstraction;
        }

        @Override
        public String toString() {
            return this.implementationClass.getSimpleName() + " implements " + this.abstraction;
        }
    }
}
