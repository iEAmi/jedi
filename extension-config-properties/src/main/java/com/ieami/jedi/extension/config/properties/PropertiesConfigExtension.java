package com.ieami.jedi.extension.config.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.extension.config.ConfigExtension;
import com.ieami.jedi.extension.config.exception.ConfigFileNotFoundException;
import com.ieami.jedi.extension.config.exception.DeserializationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * A properties base {@link ConfigExtension} implementation. this internally uses {@link ObjectMapper} to bind config
 * to model.
 * This class is aggressive in deserializing config file content.
 * so if has been any issue (such as bad file or key not found) in reading configs an exception will throw and
 * stops the application. this approach has better performance
 */
public final class PropertiesConfigExtension extends ConfigExtension<PropertiesConfigExtension> {
    private final @NotNull ClassLoader classLoader;
    private final @NotNull Map<String, String> configFileContentMap;
    private final @NotNull ObjectMapper objectMapper;

    public PropertiesConfigExtension(
            @NotNull DependencyCollection dependencyCollection,
            @NotNull String configFileName
    ) throws DeserializationException, ConfigFileNotFoundException {
        super(dependencyCollection, configFileName);
        this.objectMapper = new ObjectMapper();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.configFileContentMap = deserializeConfigFileContent(configFileName);
    }

    @Override
    protected @NotNull <I> Function<@NotNull DependencyResolver, @Nullable I> createBuilderFunction(
            @NotNull Class<I> bindToClass,
            @NotNull String prefix
    ) throws NoSuchFieldException {
        final var configJsonNode = extractedConfigNodeByPrefixWithoutPrefix(prefix);

        final var configModel = deserializeConfigNode(configJsonNode, bindToClass);

        return new ConfigHolderFunction<>(configModel);
    }

    @Override
    protected @NotNull PropertiesConfigExtension self() {
        return this;
    }

    private @NotNull Map<String, String> deserializeConfigFileContent(@NotNull String fileName) throws DeserializationException, ConfigFileNotFoundException {
        try (final var stream = classLoader.getResourceAsStream(fileName)) {
            if (stream == null)
                throw new ConfigFileNotFoundException(fileName + " not found");
            try {
                final var properties = new Properties();
                properties.load(stream);

                final var result = new HashMap<String, String>();

                for (String stringPropertyName : properties.stringPropertyNames()) {
                    final var value = properties.getProperty(stringPropertyName);
                    if (value == null)
                        continue;

                    result.put(stringPropertyName, value);
                }


                return result;
            } catch (IOException e) {
                throw new DeserializationException(e);
            }
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    private @NotNull Map<String, String> extractedConfigNodeByPrefixWithoutPrefix(@NotNull String prefix) throws NoSuchFieldException {
        final var entities = configFileContentMap.entrySet();

        final var result = new HashMap<String, String>();
        for (final var entity : entities) {
            final var key = entity.getKey();

            if (key.toLowerCase().startsWith(prefix)) {
                final var value = entity.getValue();
                final var keyWithoutPrefix = key.replaceFirst(prefix, "").replaceFirst(".", "");

                result.put(keyWithoutPrefix, value);
            }
        }

        if (result.isEmpty())
            throw new NoSuchFieldException("No property found with prefix " + prefix);

        return result;
    }

    private <I> @NotNull I deserializeConfigNode(@NotNull Map<String, String> node, @NotNull Class<I> bindToClass) {
        return objectMapper.convertValue(node, bindToClass);
    }
}
