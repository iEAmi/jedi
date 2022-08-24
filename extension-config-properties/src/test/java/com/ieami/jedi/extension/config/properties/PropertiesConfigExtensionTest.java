package com.ieami.jedi.extension.config.properties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.extension.config.exception.ConfigFileNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class PropertiesConfigExtensionTest {

    private static final class DatabaseConfig {
        public static final String PREFIX = "database.config";

        public final String connectionString;
        public final int maxPool;

        @JsonCreator
        public DatabaseConfig(
                @JsonProperty("connectionString") String connectionString,
                @JsonProperty("maxPool") int maxPool
        ) {
            this.connectionString = connectionString;
            this.maxPool = maxPool;
        }
    }

    @Test
    public void PropertiesConfigExtension_fails_fast_as_possible_if_config_file_not_found() {
        final var depCollection = DependencyCollection.newDefault();
        final ThrowingRunnable jsonConfigExtension = () -> new PropertiesConfigExtension(depCollection, "a.properties");

        Assert.assertThrows(ConfigFileNotFoundException.class, jsonConfigExtension);
    }

    @Test
    public void addConfig_register_config_model_as_singleton() {
        try {
            final var depCollection = DependencyCollection.newDefault();
            final var jsonConfigExtension = new PropertiesConfigExtension(depCollection, "application.properties");

            depCollection.extendWith(jsonConfigExtension, configRegistry -> {
                configRegistry.addConfigUnsafe(DatabaseConfig.class, DatabaseConfig.PREFIX);
            });

            final var resolver = depCollection.build();

            final var databaseConfig = resolver.resolve(DatabaseConfig.class, DatabaseConfig.class);

            Assert.assertNotNull(databaseConfig);
            Assert.assertEquals("CONNECTION_STRING", databaseConfig.connectionString);
            Assert.assertEquals(10, databaseConfig.maxPool);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
