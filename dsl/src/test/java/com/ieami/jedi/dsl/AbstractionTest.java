package com.ieami.jedi.dsl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static com.ieami.jedi.dsl.Abstraction.abstraction;

public final class AbstractionTest {

    private interface TestService {
    }

    private static class TestServiceImpl implements TestService {
    }

    @Test
    public void abstraction_factory_method_returns_not_null_instance() {
        final var abstraction = abstraction(TestService.class);
        Assert.assertNotNull(abstraction);
    }

    @Test
    public void abstraction_factory_method_throws_exception_with_null_input() {
        final ThrowingRunnable abstraction = () -> abstraction(null);
        Assert.assertThrows(IllegalArgumentException.class, abstraction);
    }
}
