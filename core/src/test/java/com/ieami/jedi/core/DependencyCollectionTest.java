package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;

public final class DependencyCollectionTest {

    private interface IA {

        String print();
    }

    private static class A implements IA {
        private final IB b;

        public A(IB b) {
            this.b = b;
        }

        @Override
        public String print() {
            return "I am A";
        }
    }

    private interface IB {
    }

    private static class B implements IB {
    }

    @Test
    public void testDsl() {
        final var beanCollection = new ReflectionDependencyCollection();
        beanCollection
                .addTransient(IA.class, A.class)
                .addTransient(IB.class, B.class);

        try {
            final var resolver = beanCollection.build();
            final var service = resolver.resolveRequired(IA.class);

            Assert.assertEquals("I am A", service.print());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
