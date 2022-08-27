package com.ieami.jedi.extension.httpListener.impl;

import com.ieami.jedi.extension.httpListener.Endpoint;
import com.ieami.jedi.extension.httpListener.GenericRpcListener;
import com.ieami.jedi.extension.httpListener.HttpMethod;
import org.jetbrains.annotations.NotNull;

public final class ArmeriaHttpServerWrapper implements GenericRpcListener<ArmeriaHttpServerWrapper> {
    @Override
    public @NotNull <Req, Res> ArmeriaHttpServerWrapper registerEndpoint(HttpMethod httpMethod, @NotNull Endpoint<Req, Res> endpoint) {
        return this;
    }
}
