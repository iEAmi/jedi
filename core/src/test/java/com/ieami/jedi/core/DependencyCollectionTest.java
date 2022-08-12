package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;

public final class DependencyCollectionTest {

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
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void duration() {
        final var beanCollection = new ReflectionDependencyCollection();
        beanCollection
                .addSingleton(IA.class, A.class)
                .addSingleton(IB.class, B.class);

        try {
            final var resolver = beanCollection.build();

            for (int i = 0; i < 1000000; i++) {
                resolver.resolveRequired(IA.class);
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void mapGetDuration() {
        final var map = new HashMap<String, A>();

        for (int i = 0; i < 1000000; i++) {
            final var t1 = System.nanoTime();

            map.get("");

            final var t2 = System.nanoTime();
            final var duration = Duration.ofNanos(t2 - t1);
            System.out.println(duration.toNanos());
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
