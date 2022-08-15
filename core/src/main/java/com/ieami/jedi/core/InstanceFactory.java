package com.ieami.jedi.core;

import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

public interface InstanceFactory {

    <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException;

    final class TransientClassReferenceNonArgumentConstructorCall implements InstanceFactory {
        private final @NotNull Constructor<?> constructor;

        public TransientClassReferenceNonArgumentConstructorCall(@NotNull Constructor<?> constructor) {
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var instance = (I) constructor.newInstance();
            return instance;
        }
    }

    final class TransientClassReferenceConstructorCall implements InstanceFactory {
        private final ExtendedDependencyResolver extendedDependencyResolver;
        private final Constructor<?> constructor;

        public TransientClassReferenceConstructorCall(
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Constructor<?> constructor) {
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            final var parameterTypes = constructor.getParameterTypes();
            final var parameterCount = parameterTypes.length;
            final var parameters = new Object[parameterCount];

            for (var i = 0; i < parameterCount; i++) {
                final var parameterType = parameterTypes[i];
                final var parameterInstance = extendedDependencyResolver.resolveRequired(parameterType);

                parameters[i] = parameterInstance;
            }

            @SuppressWarnings("unchecked") final var instance = (I) constructor.newInstance(parameters);

            return instance;
        }
    }

    final class TransientFunctionReferenceInstantiatorCall implements InstanceFactory {
        private final @NotNull ExtendedDependencyResolver extendedDependencyResolver;
        private final @NotNull Function<DependencyResolver, ?> instantiator;

        public TransientFunctionReferenceInstantiatorCall(
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Function<DependencyResolver, ?> instantiator
        ) {
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
        }

        @Override
        public <I> I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var instance = (I) instantiator.apply(extendedDependencyResolver);

            return instance;
        }
    }
}
