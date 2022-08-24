package com.ieami.jedi.extension.config.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.extension.config.exception.ConfigFileNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public final class JsonConfigExtensionTest {

    private static final class DatabaseConfig {
        public static final String KEY = "DatabaseConfig";

        public final String connectionString;
        public final int maxPool;

        @JsonCreator
        public DatabaseConfig(
                @JsonProperty("ConnectionString") String connectionString,
                @JsonProperty("MaxPool") int maxPool
        ) {
            this.connectionString = connectionString;
            this.maxPool = maxPool;
        }
    }

    @Test
    public void JsonConfigExtension_fails_fast_as_possible_if_config_file_not_found() {
        final var depCollection = DependencyCollection.newDefault();
        final ThrowingRunnable jsonConfigExtension = () -> new JsonConfigExtension(depCollection, "a.json", new ObjectMapper());

        Assert.assertThrows(ConfigFileNotFoundException.class, jsonConfigExtension);
    }

    @org.junit.Test
    public void addConfig_register_config_model_as_singleton() {
        try {
            final var depCollection = DependencyCollection.newDefault();
            final var objectMapper = new ObjectMapper();
            final var jsonConfigExtension = new JsonConfigExtension(depCollection, "appsetting.json", objectMapper);

            depCollection.extendWith(jsonConfigExtension, configRegistry -> {
                configRegistry.addConfigUnsafe(DatabaseConfig.class, DatabaseConfig.KEY);
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
