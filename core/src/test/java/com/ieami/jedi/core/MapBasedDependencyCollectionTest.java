package com.ieami.jedi.core;

import com.ieami.jedi.core.exception.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class MapBasedDependencyCollectionTest {

    private interface TestService{}
    private interface TestServiceInterfaceImpl extends TestService {}
    private static abstract class TestServiceAbstractImpl implements TestService {}
    private static class TestServiceTwoConstructorImpl implements TestService {
        public TestServiceTwoConstructorImpl(String a) {}
        public TestServiceTwoConstructorImpl(Integer a) {}
    }
    private static class TestServicePrivateConstructorImpl implements TestService {
        private TestServicePrivateConstructorImpl(Integer a) {}
    }
    private static class TestServiceProtectedConstructorImpl implements TestService {
        protected TestServiceProtectedConstructorImpl(Integer a) {}
    }
    private static class TestServiceUnknownConstructorArgumentImpl implements TestService {
        public TestServiceUnknownConstructorArgumentImpl(Integer a) {}
    }
    private static class TestServiceImpl implements TestService {}

    @Test
    public void addDependency_rejects_null_dependency() {
        final var depCollection = new MapBasedDependencyCollection();

        final ThrowingRunnable runnableToTest = () -> depCollection.addDependency(null);

        Assert.assertThrows(IllegalArgumentException.class, runnableToTest);
    }

    @Test
    public void build_rejects_interface_implementation() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServiceInterfaceImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(InterfaceImplementationException.class, runnableToTest);
    }

    @Test
    public void build_rejects_abstract_class_implementation() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServiceAbstractImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(AbstractImplementationException.class, runnableToTest);
    }

    @Test
    public void build_rejects_class_with_more_than_one_constructor() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServiceTwoConstructorImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(MoreThanOneConstructorException.class, runnableToTest);
    }

    @Test
    public void build_rejects_class_with_private_constructor() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServicePrivateConstructorImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(PrivateOrProtectedConstructorException.class, runnableToTest);
    }

    @Test
    public void build_rejects_class_with_protected_constructor() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServiceProtectedConstructorImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(PrivateOrProtectedConstructorException.class, runnableToTest);
    }

    @Test
    public void build_rejects_implementation_with_unknown_argument() {
        final var depCollection = new MapBasedDependencyCollection();
        depCollection.addTransientUnsafe(TestService.class, TestServiceUnknownConstructorArgumentImpl.class);

        final ThrowingRunnable runnableToTest = depCollection::build;

        Assert.assertThrows(UnknownDependencyException.class, runnableToTest);
    }
}
