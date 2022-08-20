package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.function.Function;

public final class DefaultFunctionReferenceImplementationTest {
    private interface TestService {}
    private static class TestServiceImpl implements TestService {
        private final String name;

        public TestServiceImpl(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void default_implementation_rejects_null_dependencyResolver_argument() {
        final ThrowingRunnable runnableToTest = () -> new Implementation.FunctionReference.Default<>(Abstraction.abstraction(TestService.class), TestServiceImpl.class, null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void default_implementation_rejects_null_abstraction_argument() {
        final ThrowingRunnable runnableToTest = () -> new Implementation.FunctionReference.Default<>(null, TestServiceImpl.class, dependencyResolver -> new TestServiceImpl("TestService"));

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void instantiator_method_returns_instance_same_as_gets_from_constructor() {
        final Function<DependencyResolver, TestServiceImpl> expectedInstantiator = dependencyResolver -> new TestServiceImpl("TestService");

        final var abstraction = Abstraction.abstraction(TestService.class);
        final var implementation = new Implementation.FunctionReference.Default<>(abstraction, TestServiceImpl.class, expectedInstantiator);

        Assert.assertEquals(expectedInstantiator, implementation.instantiator());
    }

    @Test
    public void abstraction_method_returns_instance_same_as_gets_from_constructor() {
        final var expectedAbstraction = Abstraction.abstraction(TestService.class);
        final var implementation = new Implementation.FunctionReference.Default<>(expectedAbstraction, TestServiceImpl.class, dependencyResolver -> new TestServiceImpl("TestService"));

        Assert.assertEquals(expectedAbstraction, implementation.abstraction());
    }
}
