package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public final class Abstraction<I> {
    private final @NotNull Class<I> clazz;

    public Abstraction(@NotNull Class<I> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "T class");
    }

    public static <I> @NotNull Abstraction<I> abstraction(@NotNull Class<I> clazz) {
        return new Abstraction<>(clazz);
    }

    public <Impl extends I> @NotNull Implementation<I, Impl> implementedBy(@NotNull Class<Impl> implementationClass) {
        return new Implementation.ClassReference.Default<>(implementationClass, this);
    }

    public <Impl extends I> @NotNull Implementation<I, Impl> instantiateUsing(
            @NotNull Function<@NotNull DependencyResolver, @Nullable Impl> instantiator
    ) {
        return new Implementation.FunctionReference.Default<>(this, instantiator);
    }

    public @NotNull Class<I> getAbstractionClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return this.clazz.getSimpleName();
    }
}
