package com.ieami.jedi.core;

import org.junit.Assert;
import org.junit.Test;

public final class DependencyCollectionTest {

    @Test
    public void newDefault_returns_not_null_instance() {
        final var depCollection = DependencyCollection.newDefault();

        Assert.assertNotNull(depCollection);
    }
}
