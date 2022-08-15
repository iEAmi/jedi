package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class SingletonDependencyTest {

    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
    }

    @Test
    public void singleton_implementation_rejects_null_implementationClass_argument() {
        final ThrowingRunnable runnableToTest = () -> new Dependency.Singleton<TestService, TestServiceImpl>(null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }
}
