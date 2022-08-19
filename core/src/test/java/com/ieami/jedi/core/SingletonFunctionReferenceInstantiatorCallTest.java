package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;

public final class SingletonFunctionReferenceInstantiatorCallTest {

    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
        public TestServiceImpl() {
        }
    }

    @Test
    public void create_returns_same_instance_on_each_call() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addSingleton(TestService.class, TestServiceImpl.class);

        final var resolver = depCollection.buildUnsafe();

        final var classRef = new InstanceFactory.SingletonFunctionReferenceInstantiatorCall((ExtendedDependencyResolver) resolver, dependencyResolver -> new TestServiceImpl());

        try {
            final var instance1 = classRef.<TestService>create();
            final var instance2 = classRef.<TestService>create();
            final var instance3 = classRef.<TestService>create();

            Assert.assertEquals(instance1, instance2);
            Assert.assertEquals(instance1, instance3);
            Assert.assertEquals(instance2, instance3);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
