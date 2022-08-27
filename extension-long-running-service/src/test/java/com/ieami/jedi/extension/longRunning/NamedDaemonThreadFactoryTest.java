package com.ieami.jedi.extension.longRunning;

import org.junit.Assert;
import org.junit.Test;

public final class NamedDaemonThreadFactoryTest {

    @Test
    public void newThread_returns_daemon_thread() {
        final var name = "thread_name";
        final var factory = new NamedDaemonThreadFactory(name);

        final var thread = factory.newThread(System.out::println);

        Assert.assertTrue(thread.isDaemon());

        thread.interrupt();
    }

    @Test
    public void newThread_returns_daemon_thread_with_name_starts_with_given_name() {
        final var expectedName = "thread_name";
        final var factory = new NamedDaemonThreadFactory(expectedName);

        final var thread = factory.newThread(System.out::println);

        Assert.assertTrue(thread.getName().startsWith(expectedName));

        thread.interrupt();
    }
}
