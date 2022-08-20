package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Abstraction;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

public final class CachedTransientClassReferenceConstructorCallTest {

    private interface TestRepository {
    }

    private static class TestRepositoryImpl implements TestRepository {
        public TestRepositoryImpl() {
        }
    }

    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
        public TestServiceImpl(TestRepository repository) {
        }
    }

    @Test
    public void create_returns_same_instance_on_each_call() {
        final var depCollection = DependencyCollection.newDefault();
        depCollection.addTransientUnsafe(TestRepository.class, TestRepositoryImpl.class);
        depCollection.addSingletonUnsafe(TestService.class, TestServiceImpl.class);

        final var resolver = depCollection.buildUnsafe();

        final var constructor = TestServiceImpl.class.getConstructors()[0];
        @SuppressWarnings("unchecked") final var typedConstructor = (Constructor<TestService>) constructor;
        final var abstraction = Abstraction.abstraction(TestService.class);
        final var classRef = new InstanceFactory.CachedTransientClassReferenceConstructorCall<>(abstraction, (ExtendedDependencyResolver) resolver, typedConstructor);

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
