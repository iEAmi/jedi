package com.ieami.jedi.extension.config.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieami.jedi.core.DependencyCollection;
import org.junit.Assert;

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

    @org.junit.Test
    public void syntaxTest() {
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
