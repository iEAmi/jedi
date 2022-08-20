package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import com.ieami.jedi.dsl.Dependency;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.dsl.Implementation;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MapBasedDependencyCollection implements DependencyCollection {
    private final @NotNull Map<Class<?>, Set<Dependency<?, ?>>> dependencyMap = new HashMap<>();
    private final @NotNull List<Extension> extensionList = new ArrayList<>();

    @Override
    public @NotNull <I, Impl extends I> DependencyCollection addDependency(@NotNull Dependency<I, Impl> dependency) throws DuplicateDependencyException {
        Objects.requireNonNull(dependency, "dependency");

        guardAgainstDuplicateDependency(dependency);

        final var abstractionClass = dependency.abstractionClass();

        if (!dependencyMap.containsKey(abstractionClass))
            dependencyMap.put(abstractionClass, new HashSet<>());

        dependencyMap.get(abstractionClass).add(dependency);
        return this;
    }

    @Override
    public @NotNull <E extends Extension> DependencyCollection extendWith(@NotNull E extension) {
        extensionList.add(extension);
        return this;
    }

    @Override
    public @NotNull <E extends Extension> DependencyCollection extendWith(@NotNull E extension, @NotNull Consumer<@NotNull E> builder) {
        extensionList.add(extension);
        builder.accept(extension);
        return this;
    }

    @Override
    public @NotNull <E extends Extension> DependencyCollection extendWith(@NotNull Function<@NotNull DependencyCollection, @NotNull E> f) {
        final var extension = f.apply(this);
        extensionList.add(extension);
        return this;
    }

    @Override
    public @NotNull <E extends Extension> DependencyCollection extendWith(@NotNull Function<@NotNull DependencyCollection, @NotNull E> f, @NotNull Consumer<@NotNull E> builder) {
        final var extension = f.apply(this);
        extensionList.add(extension);
        builder.accept(extension);
        return this;
    }

    @Override
    public @NotNull DependencyResolver build()
            throws
            MoreThanOneConstructorException,
            AbstractImplementationException,
            InterfaceImplementationException,
            UnknownDependencyException,
            PrivateOrProtectedConstructorException,
            ExtensionException {
        callExtensionsBeforeBuild();

        final var dependencyList = dependencyMap.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (final var dependency : dependencyList) {
            final var implementation = dependency.implementation();

            if (implementation instanceof Implementation.ClassReference)
                validateClassReferenceImplementations((Implementation.ClassReference<?, ?>) implementation);
            else if (implementation instanceof Implementation.FunctionReference)
                validateFunctionReferenceImplementations((Implementation.FunctionReference<?, ?>) implementation);
            else if (implementation instanceof Implementation.InstanceReference)
                validateInstanceReferenceImplementations((Implementation.InstanceReference<?, ?>) implementation);
        }

        final var dependencyResolver = new ReflectionExtendedDependencyResolver(dependencyMap);

        callExtensionsAfterBuild(dependencyResolver);

        return dependencyResolver;
    }

    private void callExtensionsBeforeBuild() throws ExtensionException {
        for (final var extension : extensionList) {
            extension.beforeBuild();
        }
    }

    private void callExtensionsAfterBuild(@NotNull DependencyResolver dependencyResolver) throws ExtensionException {
        for (final var extension : extensionList) {
            extension.afterBuild(dependencyResolver);
        }
    }

    private void validateFunctionReferenceImplementations(@NotNull Implementation.FunctionReference<?, ?> implementation) {
        // TODO
    }

    private void validateInstanceReferenceImplementations(@NotNull Implementation.InstanceReference<?, ?> implementation) {
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

    private <Impl extends I, I> void guardAgainstDuplicateDependency(@NotNull Dependency<I, Impl> dependency) throws DuplicateDependencyException {
        final var abstractionClass = dependency.abstractionClass();
        if (!dependencyMap.containsKey(abstractionClass)) return;

        final var implementation = dependency.implementation();
        final var dependencies = dependencyMap.get(abstractionClass);
        if (dependencies.stream().allMatch(dep -> dep.implementation().equals(implementation)))
            throw new DuplicateDependencyException(dependency + " registered before");
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
            var exactParameterTypeToCheck = parameterType;

            if (parameterType.isArray()) {
                exactParameterTypeToCheck = parameterType.getComponentType();
            }

            if (!dependencyMap.containsKey(exactParameterTypeToCheck))
                throw new UnknownDependencyException(parameterType.getCanonicalName() + " is not registered as dependency, so this is unresolvable");
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
