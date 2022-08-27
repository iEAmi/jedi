package com.ieami.jedi.extension.httpListener;

import org.jetbrains.annotations.NotNull;

public interface RpcListenerFactory<T extends GenericRpcListener<T, E>, E extends Endpoint<?, ?>> {
    @NotNull T create();
}

