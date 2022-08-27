package com.ieami.jedi.extension.httpListener.impl;

import com.ieami.jedi.extension.httpListener.GenericRpcListener;
import com.ieami.jedi.extension.httpListener.RpcListenerFactory;
import org.jetbrains.annotations.NotNull;

final class HttpServerFactory implements RpcListenerFactory {

    @Override
    public <T extends GenericRpcListener<T>> @NotNull T create() {
        return null;
    }
}
