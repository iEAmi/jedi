package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public interface Implementation<I, Impl extends I> {
    @NotNull Abstraction<I> abstraction();

    default @NotNull Dependency<I, Impl> asSingleton() {
        return new Dependency.Singleton<>(this);
    }

    default @NotNull Dependency<I, Impl> asTransient() {
        return new Dependency.Transient<>(this);
    }

    interface ClassReference<I, Impl extends I> extends Implementation<I, Impl> {
        @NotNull Class<Impl> implementationClass();

        final class Default<I, Impl extends I> implements ClassReference<I, Impl> {
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

    interface FunctionReference<I, Impl extends I> extends Implementation<I, Impl> {
        @NotNull Function<@NotNull DependencyResolver, Impl> instantiator();

        final class Default<I, Impl extends I> implements FunctionReference<I, Impl> {
            private final @NotNull Abstraction<I> abstraction;
            private final @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator;

            Default(@NotNull Abstraction<I> abstraction, @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator) {
                this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
                this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
            }

            @Override
            public @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator() {
                return instantiator;
            }

            @Override
            public @NotNull Abstraction<I> abstraction() {
                return abstraction;
            }
        }
    }

    interface InstanceReference<I, Impl extends I> extends Implementation<I, Impl> {
        @NotNull Impl instance();

        final class Default<I, Impl extends I> implements InstanceReference<I, Impl> {
            private final @NotNull Abstraction<I> abstraction;
            private final @NotNull Impl instance;

            public Default(@NotNull Abstraction<I> abstraction, @NotNull Impl instance) {
                this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
                this.instance = Objects.requireNonNull(instance, "instance");
            }

            @Override
            public @NotNull Abstraction<I> abstraction() {
                return abstraction;
            }

            @Override
            public @NotNull Impl instance() {
                return instance;
            }
        }
    }
}
