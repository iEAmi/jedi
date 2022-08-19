package com.ieami.jedi.core;

import com.ieami.jedi.dsl.DependencyResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

public interface InstanceFactory {

    <I> @Nullable I create() throws InvocationTargetException, InstantiationException, IllegalAccessException;

    final class TransientClassReferenceNonArgumentConstructorCall implements InstanceFactory {
        private final @NotNull Constructor<?> constructor;

        public TransientClassReferenceNonArgumentConstructorCall(@NotNull Constructor<?> constructor) {
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
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
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
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
        private final @NotNull Function<@NotNull DependencyResolver, ?> instantiator;

        public TransientFunctionReferenceInstantiatorCall(
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Function<@NotNull DependencyResolver, ?> instantiator
        ) {
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
        }

        @Override
        public <I> @Nullable I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var instance = (I) instantiator.apply(extendedDependencyResolver);

            return instance;
        }
    }

    final class TransientInstanceReference implements InstanceFactory {
        private final @NotNull Object instance;

        public TransientInstanceReference(@NotNull Object instance) {
            this.instance = Objects.requireNonNull(instance, "instance");
        }

        @Override
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var typedInstance = (I) instance;
            return typedInstance;
        }
    }

    final class SingletonClassReferenceNonArgumentConstructorCall implements InstanceFactory {
        private final @NotNull Constructor<?> constructor;
        private @Nullable Object instanceCache = null;

        public SingletonClassReferenceNonArgumentConstructorCall(@NotNull Constructor<?> constructor) {
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.instanceCache == null) {
                this.instanceCache = constructor.newInstance();
            }

            @SuppressWarnings("unchecked") final var typedInstance = (I) this.instanceCache;

            return typedInstance;
        }
    }

    final class SingletonClassReferenceConstructorCall implements InstanceFactory {
        private final @NotNull ExtendedDependencyResolver extendedDependencyResolver;
        private final @NotNull Constructor<?> constructor;
        private @Nullable Object instanceCache = null;

        public SingletonClassReferenceConstructorCall(
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Constructor<?> constructor) {
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.instanceCache == null) {
                this.instanceCache = createInstance();
            }

            @SuppressWarnings("unchecked") final var typedInstance = (I) this.instanceCache;

            return typedInstance;
        }

        private Object createInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            final var parameterTypes = constructor.getParameterTypes();
            final var parameterCount = parameterTypes.length;
            final var parameters = new Object[parameterCount];

            for (var i = 0; i < parameterCount; i++) {
                final var parameterType = parameterTypes[i];
                final var parameterInstance = extendedDependencyResolver.resolveRequired(parameterType);

                parameters[i] = parameterInstance;
            }

            return constructor.newInstance(parameters);
        }
    }

    final class SingletonFunctionReferenceInstantiatorCall implements InstanceFactory {
        private final @NotNull ExtendedDependencyResolver extendedDependencyResolver;
        private final @NotNull Function<@NotNull DependencyResolver, ?> instantiator;
        private @Nullable Object instanceCache = null;

        public SingletonFunctionReferenceInstantiatorCall(
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Function<@NotNull DependencyResolver, ?> instantiator
        ) {
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
        }

        @Override
        public <I> @Nullable I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.instanceCache == null) {
                this.instanceCache = instantiator.apply(extendedDependencyResolver);
            }

            @SuppressWarnings("unchecked") final var typedInstance = (I) this.instanceCache;

            return typedInstance;
        }
    }

    final class SingletonInstanceReference implements InstanceFactory {
        private final @NotNull Object instance;

        public SingletonInstanceReference(@NotNull Object instance) {
            this.instance = Objects.requireNonNull(instance, "instance");
        }

        @Override
        public <I> @NotNull I create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            @SuppressWarnings("unchecked") final var typedInstance = (I) instance;
            return typedInstance;
        }
    }
}
