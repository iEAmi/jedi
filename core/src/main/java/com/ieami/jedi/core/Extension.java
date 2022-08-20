package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.ExtensionException;
import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Extension {
    protected final @NotNull DependencyCollection dependencyCollection;

    protected Extension(@NotNull DependencyCollection dependencyCollection) {
        this.dependencyCollection = Objects.requireNonNull(dependencyCollection, "dependencyCollection");
    }

    protected void beforeBuild() throws ExtensionException {
    }

    protected void afterBuild(@NotNull DependencyResolver dependencyResolver) throws ExtensionException {
    }
}
