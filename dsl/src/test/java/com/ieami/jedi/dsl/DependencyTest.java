package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static com.ieami.jedi.dsl.Abstraction.abstraction;

public final class DependencyTest {

    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
    }

    @Test
    public void singleton_implementation_rejects_null_implementationClass_argument() {
        final ThrowingRunnable runnableToTest = () -> new Dependency.Singleton<TestService, TestServiceImpl>(null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void transient_implementation_rejects_null_implementationClass_argument() {
        final ThrowingRunnable runnableToTest = () -> new Dependency.Transient<TestService, TestServiceImpl>(null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void singleton_implementation_method_returns_same_instance_as_gets_from_constructor() {
        final var abstraction = abstraction(TestService.class);
        final var expectedImplementation = new Implementation.Default<>(TestServiceImpl.class, abstraction);

        final var dependency = new Dependency.Singleton<>(expectedImplementation);

        Assert.assertEquals(expectedImplementation, dependency.implementation());
        Assert.assertEquals(expectedImplementation.implementationClass(), dependency.implementationClass());
    }

    @Test
    public void transient_implementation_method_returns_same_instance_as_gets_from_constructor() {
        final var abstraction = abstraction(TestService.class);
        final var expectedImplementation = new Implementation.Default<>(TestServiceImpl.class, abstraction);

        final var dependency = new Dependency.Transient<>(expectedImplementation);

        Assert.assertEquals(expectedImplementation, dependency.implementation());
        Assert.assertEquals(expectedImplementation.implementationClass(), dependency.implementationClass());
    }
}
