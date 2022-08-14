package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class ReflectionDependencyResolverTest {

    private interface TestRepository {}
    private static class TestRepositoryImpl implements TestRepository {}

    private interface TestService {}
    private static class TestServiceImpl implements TestService {
        private final TestRepository repository;

        private TestServiceImpl(TestRepository repository) {
            this.repository = repository;
        }
    }

    @Test
    public void resolve_returns_null_for_unregister_abstraction() {
        final var depCollection = new ReflectionDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolve(TestService.class);

            Assert.assertNull(instance);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveRequired_throws_IllegalStateException_unregister_abstraction() {
        final var depCollection = new ReflectionDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final ThrowingRunnable runnableToTest = () -> depResolver.resolveRequired(TestService.class);

            Assert.assertThrows(IllegalStateException.class, runnableToTest);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void resolveOptional_returns_empty_Optional_unregister_abstraction() {
        final var depCollection = new ReflectionDependencyCollection();

        try {
            final var depResolver = depCollection.build();

            final var instance = depResolver.resolveOptional(TestService.class);

            Assert.assertTrue(instance.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
