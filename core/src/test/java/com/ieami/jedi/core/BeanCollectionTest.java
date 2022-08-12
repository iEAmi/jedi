package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;

public final class BeanCollectionTest {

    @Test
    public void testDsl() {
        final var beanCollection = new ReflectionDependencyCollection();
        beanCollection
                .addSingleton(IA.class, A.class)
                .addSingleton(IB.class, B.class);

        try {
            final var resolver = beanCollection.build();
            final var service = resolver.resolveRequired(IA.class);

            Assert.assertEquals("I am A", service.print());
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    public interface IA {

        String print();
    }

    public static class A implements IA {
        private final IB b;

        public A(IB b) {
            this.b = b;
        }

        @Override
        public String print() {
            return "I am A";
        }
    }

    public interface IB {
    }

    public static class B implements IB {
    }
}
