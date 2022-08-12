package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionDependencyResolver implements DependencyResolver {
    private final Map<Class<?>, Dependency<?, ?>> dependencyMap;
    private final Map<Class<?>, InstanceFactory> instanceFactoryMap = new ConcurrentHashMap<>();

    public ReflectionDependencyResolver(@NotNull Map<Class<?>, Dependency<?, ?>> dependencyMap) {
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

    @Override
    public <I> @NotNull I resolveRequired(@NotNull Class<I> abstractionClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final var instance = resolve(abstractionClass);
        if (instance == null)
            throw new IllegalStateException(abstractionClass.getCanonicalName() + " is not registered as dependency");

        return instance;
    }

    private @NotNull InstanceFactory createInstanceFactory(Dependency<?, ?> dependency) {
        final var implementationClass = dependency.implementationClass();
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0)
            return new InstanceFactory.TransientReflectionNonArgumentConstructorCall(constructor);

        return new InstanceFactory.TransientReflectionConstructorCall(this, constructor);
    }
}
