package com.ieami.jedi.core;

import com.ieami.jedi.dsl.Abstraction;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

public final class CachedTransientClassReferenceNonArgumentConstructorCallTest {

    private static class TestService {
        public TestService() {
        }
    }

    @Test
    public void create_returns_same_instance_on_each_call() {
        final var constructor = TestService.class.getConstructors()[0];
        @SuppressWarnings("unchecked") final var typedConstructor = (Constructor<TestService>) constructor;
        final var abstraction = Abstraction.abstraction(TestService.class);
        final var classRef = new InstanceFactory.CachedTransientClassReferenceNonArgumentConstructorCall<>(abstraction, typedConstructor);

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
