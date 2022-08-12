package com.ieami.jedi.core;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public interface InstanceFactory {

    <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException;

    final class TransientReflectionNonArgumentConstructorCall implements InstanceFactory {
        private final @NotNull Constructor<?> constructor;

        public TransientReflectionNonArgumentConstructorCall(@NotNull Constructor<?> constructor) {
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var instance = (I) constructor.newInstance();
            return instance;
        }
    }

    final class TransientReflectionConstructorCall implements InstanceFactory {
        private final DependencyResolver dependencyResolver;
        private final Constructor<?> constructor;

        public TransientReflectionConstructorCall(
                @NotNull DependencyResolver dependencyResolver,
                @NotNull Constructor<?> constructor) {
            this.dependencyResolver = Objects.requireNonNull(dependencyResolver, "dependencyResolver");
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            final var parameterTypes = constructor.getParameterTypes();
            final var parameterCount = parameterTypes.length;
            final var parameters = new Object[parameterCount];

            for (var i = 0; i < parameterCount; i++) {
                final var parameterType = parameterTypes[i];
                final var parameterInstance = dependencyResolver.resolveRequired(parameterType);

                parameters[i] = parameterInstance;
            }

            @SuppressWarnings("unchecked") final var instance = (I) constructor.newInstance(parameters);

            return instance;
        }
    }
}
