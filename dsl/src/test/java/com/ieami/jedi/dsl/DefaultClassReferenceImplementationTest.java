package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class DefaultClassReferenceImplementationTest {
    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
    }

    @Test
    public void default_implementation_rejects_null_implementationClass_argument() {
        final ThrowingRunnable runnableToTest = () -> new Implementation.ClassReference.Default<TestService, TestServiceImpl>(null, null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void default_implementation_rejects_null_abstraction_argument() {
        final ThrowingRunnable runnableToTest = () -> new Implementation.ClassReference.Default<>(TestServiceImpl.class, null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void implementationClass_method_returns_instance_same_as_gets_from_constructor() {
        final var expectedImplementationClass = TestServiceImpl.class;

        final var abstraction = Abstraction.abstraction(TestService.class);
        final var implementation =  new Implementation.ClassReference.Default<>(expectedImplementationClass, abstraction);

        Assert.assertEquals(expectedImplementationClass, implementation.implementationClass());
    }

    @Test
    public void abstraction_method_returns_instance_same_as_gets_from_constructor() {
        final var expectedAbstraction = Abstraction.abstraction(TestService.class);
        final var implementation =  new Implementation.ClassReference.Default<>(TestServiceImpl.class, expectedAbstraction);

        Assert.assertEquals(expectedAbstraction, implementation.abstraction());
    }
}
