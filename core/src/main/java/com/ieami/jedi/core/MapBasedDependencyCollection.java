package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.Implementation;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MapBasedDependencyCollection implements DependencyCollection {
    private final Map<Class<?>, Dependency<?, ?>> dependencyMap = new HashMap<>();

    @Override
    public @NotNull <I, Impl extends I> DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency) {
        Objects.requireNonNull(dependency, "dependency");

        final var abstractionClass = dependency.abstractionClass();
        dependencyMap.put(abstractionClass, dependency);
        return this;
    }

    @Override
    public @NotNull ExtendedDependencyResolver build() throws MoreThanOneConstructorException, AbstractImplementationException, InterfaceImplementationException, UnknownDependencyException, PrivateOrProtectedConstructorException {
        final var dependencyList = dependencyMap.values();

        for (final var dependency : dependencyList) {
            final var implementation = dependency.implementation();

            if (implementation instanceof Implementation.ClassReference)
                validateClassReferenceImplementations((Implementation.ClassReference<?, ?>) implementation);
            else if (implementation instanceof Implementation.FunctionReference)
                validateFunctionReferenceImplementations((Implementation.FunctionReference<?, ?>) implementation);

        }

        return new ReflectionExtendedDependencyResolver(dependencyMap);
    }

    private void validateFunctionReferenceImplementations(@NotNull Implementation.FunctionReference<?, ?> implementation) {
        // TODO
    }

    private void validateClassReferenceImplementations(@NotNull Implementation.ClassReference<?, ?> implementation)
            throws AbstractImplementationException, InterfaceImplementationException, MoreThanOneConstructorException, UnknownDependencyException, PrivateOrProtectedConstructorException {
        final var implementationClass = implementation.implementationClass();

        guardAgainstInterfaceImplementation(implementationClass);
        guardAgainstAbstractClassImplementation(implementationClass);
        guardAgainstClassWithMoreThanOneConstructor(implementationClass);
        guardAgainstImplementationClassWithNonPublicConstructor(implementationClass);
        guardAgainstClassesWithUnknownArgument(implementationClass);
    }

    private void guardAgainstInterfaceImplementation(
            @NotNull Class<?> implementationClass
    ) throws InterfaceImplementationException {
        final var modifiers = implementationClass.getModifiers();

        if (Modifier.isInterface(modifiers))
            throw new InterfaceImplementationException(implementationClass.getCanonicalName() + " is an interface and could instantiate.");
    }

    private void guardAgainstAbstractClassImplementation(
            @NotNull Class<?> implementationClass
    ) throws AbstractImplementationException {
        final var modifiers = implementationClass.getModifiers();

        if (Modifier.isAbstract(modifiers))
            throw new AbstractImplementationException(implementationClass.getCanonicalName() + " is a abstract class and could instantiate.");
    }

    private void guardAgainstClassWithMoreThanOneConstructor(
            @NotNull Class<?> implementationClass
    ) throws MoreThanOneConstructorException {
        final var constructors = implementationClass.getConstructors();

        if (constructors.length > 1)
            throw new MoreThanOneConstructorException(implementationClass.getCanonicalName() + " has " + constructors.length + " constructor. this is not supported yet");
    }

    private void guardAgainstClassesWithUnknownArgument(
            @NotNull Class<?> implementationClass
    ) throws UnknownDependencyException {
        final var constructor = implementationClass.getConstructors()[0];
        final var parameterTypes = constructor.getParameterTypes();

        for (final var parameterType : parameterTypes) {
            if (!dependencyMap.containsKey(parameterType))
                throw new UnknownDependencyException(parameterType.getCanonicalName() + " is not registered as dependency, so this is un resolvable");
        }
    }

    private void guardAgainstImplementationClassWithNonPublicConstructor(
            @NotNull Class<?> implementationClass
    ) throws PrivateOrProtectedConstructorException {
        final var constructors = implementationClass.getConstructors();

        if (constructors.length == 0)
            throw new PrivateOrProtectedConstructorException(implementationClass + " has one private/protected constructor. private/protected constructors not supported.");
    }
}
