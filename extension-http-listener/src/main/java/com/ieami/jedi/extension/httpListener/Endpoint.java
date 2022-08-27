package com.ieami.jedi.extension.httpListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface Endpoint<Req, Res> extends Function<@Nullable Req, @NotNull CompletionStage<@NotNull Res>> {
    @NotNull CompletionStage<@NotNull Res> handle(@Nullable Req request) throws Exception;

    @Override
    default @NotNull CompletionStage<@NotNull Res> apply(@Nullable Req req) {
        try {
            return handle(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
