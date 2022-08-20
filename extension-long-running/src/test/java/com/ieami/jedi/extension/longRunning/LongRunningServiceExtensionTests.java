package com.ieami.jedi.extension.longRunning;

import com.ieami.jedi.core.DependencyCollection;
import org.junit.Test;

public final class LongRunningServiceExtensionTests {

    private static final class BackgroundService implements Runnable {
        public BackgroundService() {
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Hi I am here");
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void syntax_test() {
        final var depCollection = DependencyCollection.newDefault();
        final var extension = new LongRunningServiceExtension(depCollection);
        depCollection.extendWith(extension, longRunningServiceExtension -> {
            longRunningServiceExtension.addLongRunningServiceUnsafe(BackgroundService.class);
        });

        final var resolver = depCollection.buildUnsafe();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
