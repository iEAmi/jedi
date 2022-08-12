package com.ieami.jedi.dsl;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Abstraction<I> {
    private final @NotNull Class<I> clazz;

    public Abstraction(@NotNull Class<I> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "T class");
    }

    public static <I> @NotNull Abstraction<I> abstraction(@NotNull Class<I> clazz) {
        return new Abstraction<>(clazz);
    }

    public <Impl extends I> @NotNull Implementation<I, Impl> implementedBy(@NotNull Class<Impl> implementationClass) {
        return new Implementation.Default<>(implementationClass, this);
    }

    @Override
    public String toString() {
        return this.clazz.getSimpleName();
    }
}
