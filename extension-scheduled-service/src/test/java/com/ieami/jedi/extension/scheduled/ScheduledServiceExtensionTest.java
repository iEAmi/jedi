package com.ieami.jedi.extension.scheduled;

import com.ieami.jedi.core.DependencyCollection;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public final class ScheduledServiceExtensionTest {

    private static final class TestScheduledService implements Runnable {
        public TestScheduledService() {

        }

        @Override
        public void run() {
            System.out.println("I am run after 2 sec");
        }
    }

    private static final class TestScheduledAtFixedRateService implements Runnable {
        public TestScheduledAtFixedRateService() {

        }

        @Override
        public void run() {
            System.out.println("Hello started after 1 sec and scheduled at 2 sec");
        }
    }

    @Test
    public void schedule_afterDelay_syntax_test() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledService.class)
                    .afterDelayUnsafe(2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void schedule_afterDelay_cause_implementation_registered_as_singleton() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledService.class)
                    .afterDelayUnsafe(2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        final var instance_1 = dependencyResolver.resolveUnsafe(TestScheduledService.class);
        final var instance_2 = dependencyResolver.resolveUnsafe(TestScheduledService.class);

        Assert.assertNotNull(instance_1);
        Assert.assertNotNull(instance_2);
        Assert.assertEquals(instance_1, instance_2); // should be singleton
    }

    @Test
    public void schedule_atFixedRate_syntax_test() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledAtFixedRateService.class)
                    .atFixedRateUnsafe(1, 2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void schedule_atFixedRate_cause_implementation_registered_as_singleton() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledAtFixedRateService.class)
                    .atFixedRateUnsafe(1, 2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        final var instance_1 = dependencyResolver.resolveUnsafe(TestScheduledAtFixedRateService.class);
        final var instance_2 = dependencyResolver.resolveUnsafe(TestScheduledAtFixedRateService.class);

        Assert.assertNotNull(instance_1);
        Assert.assertNotNull(instance_2);
        Assert.assertEquals(instance_1, instance_2); // should be singleton
    }

    @Test
    public void schedule_withFixedDelay_syntax_test() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledAtFixedRateService.class)
                    .withFixedDelayUnsafe(1, 2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void schedule_withFixedDelay_cause_implementation_registered_as_singleton() {
        final var depCollection = DependencyCollection.newDefault();

        depCollection.extendWith(new ScheduledServiceExtension(depCollection), scheduledServiceExtension -> {
            scheduledServiceExtension
                    .schedule(TestScheduledAtFixedRateService.class)
                    .withFixedDelayUnsafe(1, 2, TimeUnit.SECONDS);
        });

        final var dependencyResolver = depCollection.buildUnsafe();

        final var instance_1 = dependencyResolver.resolveUnsafe(TestScheduledAtFixedRateService.class);
        final var instance_2 = dependencyResolver.resolveUnsafe(TestScheduledAtFixedRateService.class);

        Assert.assertNotNull(instance_1);
        Assert.assertNotNull(instance_2);
        Assert.assertEquals(instance_1, instance_2); // should be singleton
    }
}
