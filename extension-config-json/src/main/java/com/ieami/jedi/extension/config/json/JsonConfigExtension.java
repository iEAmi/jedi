package com.ieami.jedi.extension.config.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ieami.jedi.core.DependencyCollection;
import com.ieami.jedi.dsl.DependencyResolver;
import com.ieami.jedi.extension.config.ConfigExtension;
import com.ieami.jedi.extension.config.exception.ConfigFileNotFoundException;
import com.ieami.jedi.extension.config.exception.DeserializationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * A jackson base {@link ConfigExtension} implementation.
 * This class is aggressive in deserializing config file content, so gives an {@link ObjectMapper} instance
 * from constructor instead of resolving from {@link DependencyResolver} to deserialize config file content ASAP.
 * so if has been any issue (such as bad json file or key not found) in reading configs an exception will throw and
 * stops the application. this approach has better performance
 */
public final class JsonConfigExtension extends ConfigExtension<JsonConfigExtension> {
    private final @NotNull ObjectMapper objectMapper;
    private final @NotNull JsonNode configFileContentJsonNode;
    private final @NotNull ClassLoader classLoader;

    public JsonConfigExtension(
            @NotNull DependencyCollection dependencyCollection,
            @NotNull String configFileName,
            @NotNull ObjectMapper objectMapper
    ) throws DeserializationException, ConfigFileNotFoundException {
        super(dependencyCollection, configFileName);
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.configFileContentJsonNode = deserializeConfigFileContent(configFileName);
    }

    @Override
    protected @NotNull <I> Function<@NotNull DependencyResolver, @Nullable I> createBuilderFunction(
            @NotNull Class<I> bindToClass,
            @NotNull String key
    ) throws NoSuchFieldException, DeserializationException {
        final var configJsonNode = extractedConfigJsonNodeByKey(key);

        final var configModel = deserializeConfigJsonNode(configJsonNode, bindToClass);

        return new ConfigHolderFunction<>(configModel);
    }

    @Override
    protected @NotNull JsonConfigExtension self() {
        return this;
    }

    private @NotNull JsonNode deserializeConfigFileContent(@NotNull String fileName) throws DeserializationException, ConfigFileNotFoundException {
        try (final var stream = classLoader.getResourceAsStream(fileName)) {
            if (stream == null)
                throw new ConfigFileNotFoundException(fileName + " not found");
            try {
                return objectMapper.readTree(stream);
            } catch (IOException e) {
                throw new DeserializationException(e);
            }
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    private @NotNull JsonNode extractedConfigJsonNodeByKey(@NotNull String key) throws NoSuchFieldException {
        final var configJsonNode = configFileContentJsonNode.get(key);
        if (configJsonNode == null)
            throw new NoSuchFieldException("Field " + key + " is not declared in file + " + configFileName);

        return configJsonNode;
    }

    private <I> @NotNull I deserializeConfigJsonNode(@NotNull JsonNode jsonNode, @NotNull Class<I> bindToClass) throws DeserializationException {
        try {
            return objectMapper.treeToValue(jsonNode, bindToClass);
        } catch (JsonProcessingException e) {
            throw new DeserializationException(e);
        }
    }
}
