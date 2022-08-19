package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionExtendedDependencyResolver implements ExtendedDependencyResolver {
    private final Map<Class<?>, Dependency<?, ?>> dependencyMap;
    private final Map<Class<?>, InstanceFactory> instanceFactoryMap = new ConcurrentHashMap<>();

    public ReflectionExtendedDependencyResolver(@NotNull Map<Class<?>, Dependency<?, ?>> dependencyMap) {
        Objects.requireNonNull(dependencyMap, "dependencyMap");
        this.dependencyMap = new ConcurrentHashMap<>(dependencyMap);
    }

    @Override
    public <I> @Nullable I resolve(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var dependency = dependencyMap.get(abstractionClass);
        if (dependency == null)
            return null;

        final var instanceFactory = instanceFactoryMap.computeIfAbsent(abstractionClass, aClass -> createInstanceFactory(dependency));
        return instanceFactory.create();
    }

    private @NotNull InstanceFactory createInstanceFactory(@NotNull Dependency<?, ?> dependency) {
        if (dependency instanceof Dependency.Transient)
            return createTransientInstanceFactory((Dependency.Transient<?, ?>) dependency);
        else if (dependency instanceof Dependency.Singleton)
            return createSingletonInstanceFactory((Dependency.Singleton<?, ?>) dependency);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory createTransientInstanceFactory(@NotNull Dependency.Transient<?, ?> dependency) {
        final var implementation = dependency.implementation();
        if (implementation instanceof Implementation.ClassReference)
            return createTransientClassReferenceInstanceFactory((Implementation.ClassReference<?, ?>) implementation);

        if (implementation instanceof Implementation.FunctionReference)
            return createTransientFunctionReferenceInstanceFactory((Implementation.FunctionReference<?, ?>) implementation);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory createSingletonInstanceFactory(@NotNull Dependency.Singleton<?, ?> dependency) {
        final var implementation = dependency.implementation();
        if (implementation instanceof Implementation.ClassReference)
            return createSingletonClassReferenceInstanceFactory((Implementation.ClassReference<?, ?>) implementation);

        if (implementation instanceof Implementation.FunctionReference)
            return createSingletonFunctionReferenceInstanceFactory((Implementation.FunctionReference<?, ?>) implementation);

        // Never happened
        throw new IllegalStateException();
    }

    private @NotNull InstanceFactory createTransientClassReferenceInstanceFactory(
            @NotNull Implementation.ClassReference<?, ?> implementation
    ) {
        final var implementationClass = implementation.implementationClass();
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0)
            return new InstanceFactory.TransientClassReferenceNonArgumentConstructorCall(constructor);

        return new InstanceFactory.TransientClassReferenceConstructorCall(this, constructor);
    }

    private @NotNull InstanceFactory createTransientFunctionReferenceInstanceFactory(
            @NotNull Implementation.FunctionReference<?, ?> implementation
    ) {
        final var instantiator = implementation.instantiator();
        return new InstanceFactory.TransientFunctionReferenceInstantiatorCall(this, instantiator);
    }

    private @NotNull InstanceFactory createSingletonFunctionReferenceInstanceFactory(
            @NotNull Implementation.FunctionReference<?, ?> implementation
    ) {
        final var instantiator = implementation.instantiator();
        return new InstanceFactory.SingletonFunctionReferenceInstantiatorCall(this, instantiator);
    }

    private @NotNull InstanceFactory createSingletonClassReferenceInstanceFactory(
            @NotNull Implementation.ClassReference<?, ?> implementation
    ) {
        final var implementationClass = implementation.implementationClass();
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0)
            return new InstanceFactory.SingletonClassReferenceNonArgumentConstructorCall(constructor);

        return new InstanceFactory.SingletonClassReferenceConstructorCall(this, constructor);
    }
}
