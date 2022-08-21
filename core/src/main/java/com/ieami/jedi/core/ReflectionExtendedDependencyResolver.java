package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.Implementation;
import com.ieami.jedi.dsl.exception.MoreThanOneDependencyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ReflectionExtendedDependencyResolver implements ExtendedDependencyResolver {
    private final Map<DependencyKey, Dependency<?, ?>> dependencyMap;
    private final Map<DependencyKey, InstanceFactory<?, ?>> instanceFactoryMap = new ConcurrentHashMap<>();

    public ReflectionExtendedDependencyResolver(@NotNull Map<Class<?>, Set<Dependency<?, ?>>> dependencyMap) {
        Objects.requireNonNull(dependencyMap, "dependencyMap");
        this.dependencyMap = new ConcurrentHashMap<>(convertDependencyMap(dependencyMap));
    }

    @Override
    public <I, Impl extends I> @Nullable Impl resolve(
            @NotNull Class<I> abstractionClass,
            @NotNull Class<Impl> implementationClass
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        if (abstractionClass.equals(implementationClass)) {
            final var dependencies = resolveAll(abstractionClass);
            if (dependencies.length == 0)
                return null;
            if (dependencies.length > 1)
                throw new MoreThanOneDependencyException(abstractionClass + " has " + dependencies.length + " registered implementation. user resolve(abstractionClass, implementationClass) to resolve particular implementation");

            @SuppressWarnings("unchecked") final var instance = (Impl) dependencies[0];
            return instance;
        }

        final var key = createKeyForAbstractionImplementationPair(abstractionClass, implementationClass);
        final var dependency = dependencyMap.get(key);
        if (dependency == null)
            return null;

        @SuppressWarnings("unchecked") final var typedDependency = (Dependency<I, Impl>) dependency;
        return resolve(typedDependency);
    }

    @Override
    public <I> @Nullable I[] resolveAll(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException, MoreThanOneDependencyException {
        final var dependencies = dependencyMap.values()
                .stream()
                .filter(dependency -> dependency.abstractionClass().equals(abstractionClass))
                .collect(Collectors.toList());

        @SuppressWarnings("unchecked") final var instances = (I[]) Array.newInstance(abstractionClass, dependencies.size());

        for (int i = 0; i < dependencies.size(); i++) {
            final var dependency = dependencies.get(i);
            instances[i] = (I) resolve(dependency);
        }

        return instances;
    }

    private @NotNull Map<DependencyKey, Dependency<?, ?>> convertDependencyMap(
            @NotNull Map<Class<?>, Set<Dependency<?, ?>>> dependencyMap
    ) {
        final var result = new HashMap<DependencyKey, Dependency<?, ?>>();

        for (final var dependencies : dependencyMap.values()) {
            for (final var dependency : dependencies) {
                final var abstractionClass = dependency.abstractionClass();
                final var implementation = dependency.implementation();
                final var implementationClass = implementation.implementationClass();
                final var key = createKeyForAbstractionImplementationPair(abstractionClass, implementationClass);

                result.put(key, dependency);
            }
        }

        return result;
    }

    private <I, Impl extends I> @Nullable Impl resolve(@NotNull Dependency<I, Impl> dependency) throws InvocationTargetException, MoreThanOneDependencyException, InstantiationException, IllegalAccessException {
        final var abstractionClass = dependency.abstractionClass();
        final var implementationClass = dependency.implementationClass();

        final var key = new ImplementationClassAbstractionClassKey<>(abstractionClass, implementationClass);
        final var instanceFactory = instanceFactoryMap.computeIfAbsent(key, aClass -> createInstanceFactory(dependency));
        return (Impl) instanceFactory.create();
    }

    private @NotNull InstanceFactory<?, ?> createInstanceFactory(@NotNull Dependency<?, ?> dependency) {
        if (dependency instanceof Dependency.Transient)
            return createTransientInstanceFactory((Dependency.Transient<?, ?>) dependency);
        else if (dependency instanceof Dependency.Singleton)
            return createSingletonInstanceFactory((Dependency.Singleton<?, ?>) dependency);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory<?, ?> createTransientInstanceFactory(@NotNull Dependency.Transient<?, ?> dependency) {
        final var implementation = dependency.implementation();
        if (implementation instanceof Implementation.ClassReference)
            return createTransientClassReferenceInstanceFactory((Implementation.ClassReference<?, ?>) implementation);

        if (implementation instanceof Implementation.FunctionReference)
            return createTransientFunctionReferenceInstanceFactory((Implementation.FunctionReference<?, ?>) implementation);

        if (implementation instanceof Implementation.InstanceReference)
            return createTransientInstanceReferenceInstanceFactory((Implementation.InstanceReference<?, ?>) implementation);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory<?, ?> createSingletonInstanceFactory(@NotNull Dependency.Singleton<?, ?> dependency) {
        final var implementation = dependency.implementation();
        if (implementation instanceof Implementation.ClassReference)
            return createSingletonClassReferenceInstanceFactory((Implementation.ClassReference<?, ?>) implementation);

        if (implementation instanceof Implementation.FunctionReference)
            return createSingletonFunctionReferenceInstanceFactory((Implementation.FunctionReference<?, ?>) implementation);

        if (implementation instanceof Implementation.InstanceReference)
            return createSingletonInstanceReferenceInstanceFactory((Implementation.InstanceReference<?, ?>) implementation);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory<?, ?> createTransientClassReferenceInstanceFactory(
            @NotNull Implementation.ClassReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var implementationClass = implementation.implementationClass();
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.TransientClassReferenceNonArgumentConstructorCall(abstraction, constructor);
            return instanceFactory;
        }

        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.TransientClassReferenceConstructorCall(abstraction, this, constructor);
        return instanceFactory;
    }

    private @NotNull InstanceFactory<?, ?> createTransientFunctionReferenceInstanceFactory(
            @NotNull Implementation.FunctionReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var instantiator = implementation.instantiator();
        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.TransientFunctionReferenceInstantiatorCall(abstraction, this, instantiator);
        return instanceFactory;
    }

    private @NotNull InstanceFactory<?, ?> createTransientInstanceReferenceInstanceFactory(
            @NotNull Implementation.InstanceReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var instance = implementation.instance();
        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.InstanceReference(abstraction, instance);
        return instanceFactory;
    }

    private @NotNull InstanceFactory<?, ?> createSingletonFunctionReferenceInstanceFactory(
            @NotNull Implementation.FunctionReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var instantiator = implementation.instantiator();
        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.CachedTransientFunctionReferenceInstantiatorCall(abstraction, this, instantiator);
        return instanceFactory;
    }

    private @NotNull InstanceFactory<?, ?> createSingletonClassReferenceInstanceFactory(
            @NotNull Implementation.ClassReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var implementationClass = implementation.implementationClass();
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.CachedTransientClassReferenceNonArgumentConstructorCall(abstraction, constructor);
            return instanceFactory;
        }

        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.CachedTransientClassReferenceConstructorCall(abstraction, this, constructor);
        return instanceFactory;
    }

    private @NotNull InstanceFactory<?, ?> createSingletonInstanceReferenceInstanceFactory(
            @NotNull Implementation.InstanceReference<?, ?> implementation
    ) {
        final var abstraction = implementation.abstraction();
        final var instance = implementation.instance();
        @SuppressWarnings({"unchecked", "rawtypes"}) final var instanceFactory = new InstanceFactory.InstanceReference(abstraction, instance);
        return instanceFactory;
    }

    private @NotNull DependencyKey createKeyForAbstractionImplementationPair(
            @NotNull Class<?> abstractionClass,
            @NotNull Class<?> implementationClass
    ) {
        if (isAbstractionGeneric(abstractionClass)) {
            final var genericTypeParameters = typeParametersOfImplementation(abstractionClass, implementationClass);
            @SuppressWarnings({"unchecked", "rawtypes"}) final var key = new ImplementationClassAbstractionClassGenericParameterKey(abstractionClass, implementationClass, genericTypeParameters);
            return key;
        } else {
            @SuppressWarnings({"unchecked", "rawtypes"}) final var key = new ImplementationClassAbstractionClassKey(abstractionClass, implementationClass);
            return key;
        }
    }

    private boolean isAbstractionGeneric(@NotNull Class<?> abstractionClass) {
        return abstractionClass.getTypeParameters().length != 0;
    }

    private @NotNull Class<?>[] typeParametersOfImplementation(@NotNull Class<?> abstractionClass, @NotNull Class<?> implementationClass) {
        final var superClass = (ParameterizedType) (abstractionClass.isInterface()
                ? Arrays.stream(implementationClass.getGenericInterfaces()).filter(type -> ((ParameterizedType) type).getRawType().equals(abstractionClass)).findFirst().get()
                : implementationClass.getGenericSuperclass());

        return Arrays.stream(superClass.getActualTypeArguments())
                .map(type -> (Class<?>) type)
                .toArray(Class[]::new);
    }

    private interface DependencyKey {
    }

    private static final class ImplementationClassAbstractionClassKey<I, Impl extends I> implements DependencyKey {
        private final @NotNull Class<I> abstractionClass;
        private final @NotNull Class<Impl> implementationClass;

        private ImplementationClassAbstractionClassKey(
                @NotNull Class<I> abstractionClass,
                @NotNull Class<Impl> implementationClass
        ) {
            this.abstractionClass = Objects.requireNonNull(abstractionClass, "abstractionClass");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final var that = (ImplementationClassAbstractionClassKey<?, ?>) o;
            return abstractionClass.equals(that.abstractionClass) && implementationClass.equals(that.implementationClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(abstractionClass, implementationClass);
        }
    }

    private static final class ImplementationClassAbstractionClassGenericParameterKey<I, Impl extends I> implements DependencyKey {
        private final @NotNull Class<I> abstractionClass;
        private final @NotNull Class<Impl> implementationClass;
        private final @NotNull Class<?>[] implementationTypeParameters;

        private ImplementationClassAbstractionClassGenericParameterKey(
                @NotNull Class<I> abstractionClass,
                @NotNull Class<Impl> implementationClass,
                @NotNull Class<?>[] implementationTypeParameters
        ) {
            this.abstractionClass = Objects.requireNonNull(abstractionClass, "abstractionClass");
            this.implementationClass = Objects.requireNonNull(implementationClass, "implementationClass");
            this.implementationTypeParameters = Objects.requireNonNull(implementationTypeParameters, "implementationTypeParameters");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final var that = (ImplementationClassAbstractionClassGenericParameterKey<?, ?>) o;
            return abstractionClass.equals(that.abstractionClass)
                    && implementationClass.equals(that.implementationClass)
                    && Arrays.equals(implementationTypeParameters, that.implementationTypeParameters);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(abstractionClass, implementationClass);
            result = 31 * result + Arrays.hashCode(implementationTypeParameters);
            return result;
        }
    }
}
