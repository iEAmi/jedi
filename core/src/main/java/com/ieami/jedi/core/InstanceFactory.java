package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Abstraction;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

public interface InstanceFactory<I, Impl extends I> {

    @NotNull Abstraction<I> abstraction();

    default @NotNull Class<I> abstractionClass() {
        return abstraction().getAbstractionClass();
    }

    @Nullable Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException;

    final class TransientClassReferenceNonArgumentConstructorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull Abstraction<I> abstraction;
        private final @NotNull Constructor<Impl> constructor;

        public TransientClassReferenceNonArgumentConstructorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull Constructor<Impl> constructor
        ) {
            this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.abstraction;
        }

        @Override
        public @NotNull Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
    }

    final class TransientClassReferenceConstructorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull Abstraction<I> abstraction;
        private final @NotNull ExtendedDependencyResolver extendedDependencyResolver;
        private final @NotNull Constructor<Impl> constructor;

        public TransientClassReferenceConstructorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Constructor<Impl> constructor) {
            this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.constructor = Objects.requireNonNull(constructor, "constructor");
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.abstraction;
        }

        @Override
        public @NotNull Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
            final var parameterTypes = constructor.getParameterTypes();
            final var parameterCount = parameterTypes.length;
            final var parameters = new Object[parameterCount];

            for (var i = 0; i < parameterCount; i++) {
                final var parameterType = parameterTypes[i];

                final Object parameterInstance;
                if (parameterType.isArray()) {
                    final var d = parameterType.cast(extendedDependencyResolver.resolveAllRequired(parameterType.getComponentType()));
                    parameterInstance = d;
                } else
                    parameterInstance = extendedDependencyResolver.resolveRequired(parameterType);


                parameters[i] = parameterInstance;
            }

            return constructor.newInstance(parameters);
        }
    }

    final class TransientFunctionReferenceInstantiatorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull Abstraction<I> abstraction;
        private final @NotNull ExtendedDependencyResolver extendedDependencyResolver;
        private final @NotNull Function<@NotNull DependencyResolver, Impl> instantiator;

        public TransientFunctionReferenceInstantiatorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Function<@NotNull DependencyResolver, Impl> instantiator
        ) {
            this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
            this.extendedDependencyResolver = Objects.requireNonNull(extendedDependencyResolver, "dependencyResolver");
            this.instantiator = Objects.requireNonNull(instantiator, "instantiator");
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.abstraction;
        }

        @Override
        public @Nullable Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            return instantiator.apply(extendedDependencyResolver);
        }
    }

    final class InstanceReference<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull Abstraction<I> abstraction;
        private final @NotNull Impl instance;

        public InstanceReference(@NotNull Abstraction<I> abstraction, @NotNull Impl instance) {
            this.abstraction = Objects.requireNonNull(abstraction, "abstraction");
            this.instance = Objects.requireNonNull(instance, "instance");
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.abstraction;
        }

        @Override
        public @NotNull Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            return instance;
        }
    }

    final class CachedTransientClassReferenceNonArgumentConstructorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull TransientClassReferenceNonArgumentConstructorCall<I, Impl> instanceFactory;
        private @Nullable Impl instanceCache = null;

        public CachedTransientClassReferenceNonArgumentConstructorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull Constructor<Impl> constructor
        ) {
            this.instanceFactory = new TransientClassReferenceNonArgumentConstructorCall<>(abstraction, constructor);
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return instanceFactory.abstraction();
        }

        @Override
        public @NotNull Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.instanceCache == null) {
                this.instanceCache = instanceFactory.create();
            }

            return this.instanceCache;
        }
    }

    final class CachedTransientClassReferenceConstructorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull TransientClassReferenceConstructorCall<I, Impl> instanceFactory;
        private @Nullable Impl instanceCache = null;

        public CachedTransientClassReferenceConstructorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Constructor<Impl> constructor
        ) {
            this.instanceFactory = new InstanceFactory.TransientClassReferenceConstructorCall<>(abstraction, extendedDependencyResolver, constructor);
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.instanceFactory.abstraction;
        }

        @Override
        public @NotNull Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
            if (this.instanceCache == null) {
                this.instanceCache = this.instanceFactory.create();
            }

            return this.instanceCache;
        }
    }

    final class CachedTransientFunctionReferenceInstantiatorCall<I, Impl extends I> implements InstanceFactory<I, Impl> {
        private final @NotNull TransientFunctionReferenceInstantiatorCall<I, Impl> instanceFactory;
        private @Nullable Impl instanceCache = null;

        public CachedTransientFunctionReferenceInstantiatorCall(
                @NotNull Abstraction<I> abstraction,
                @NotNull ExtendedDependencyResolver extendedDependencyResolver,
                @NotNull Function<@NotNull DependencyResolver, Impl> instantiator
        ) {
            this.instanceFactory = new TransientFunctionReferenceInstantiatorCall<>(abstraction, extendedDependencyResolver, instantiator);
        }

        @Override
        public @NotNull Abstraction<I> abstraction() {
            return this.instanceFactory.abstraction();
        }

        @Override
        public @Nullable Impl create() throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (this.instanceCache == null) {
                this.instanceCache = instanceFactory.create();
            }

            return this.instanceCache;
        }
    }
}
