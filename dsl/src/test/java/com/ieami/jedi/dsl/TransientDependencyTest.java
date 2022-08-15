package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class TransientDependencyTest {
    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
    }

    @Test
    public void transient_implementation_rejects_null_implementationClass_argument() {
        final ThrowingRunnable runnableToTest = () -> new Dependency.Transient<TestService, TestServiceImpl>(null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }
}
