package com.ieami.jedi.extension.httpListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

public interface GenericRpcListener<T extends GenericRpcListener<T, E>, E extends Endpoint<?, ?>> {

    @NotNull <Req, Res> T registerEndpoint(@NotNull E endpoint);

    void start() throws Exception;

    void stop() throws Exception;
}

class Main {

    private static final class GetContactRequest {

    }

    private static final class GetContactResponse {

    }

    public static void main(String[] args) {
        final var factory = new ArmeriaHttpServerFactory();
        final HttpServer<ArmeriaHttpServer, HttpEndpoint<?, ?>> listener = factory.create();

        listener.prefix("health", routeDefinition -> {
            routeDefinition
                    .on("ready")
                    .get(fromPath(GetContactRequest.class)).returns(request ->);

            routeDefinition
                    .on("live")
                    .get(fromPath(GetContactRequest.class)).returns(request ->);
        });
    }
}

interface EndpointDefinition {
    <Req> EndpointDefinition get(Class<Req> reqClass);

    EndpointDefinition post();
}

interface RouteDefinition {
    @NotNull RouteDefinition prefix(@NotNull String prefix, @NotNull Consumer<RouteDefinition> definition);

    @NotNull EndpointDefinition on(@NotNull String path);
}

interface HttpServer<T extends HttpServer<T, E>, E extends HttpEndpoint<?, ?>> extends GenericRpcListener<T, E> {
    @NotNull HttpServer<T, E> prefix(@NotNull String prefix, @NotNull Consumer<RouteDefinition> definition);
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

interface HttpEndpoint<Req, Res> extends Endpoint<Req, Res> {

    static <Req, Res> HttpEndpoint.Get<Req, Res> get(@NotNull Function<Req, CompletionStage<Res>> f) {
    }

    @NotNull HttpMethod method();

    interface Get<Req, Res> extends HttpEndpoint<Req, Res> {
        @Override
        default @NotNull HttpMethod method() {
            return HttpMethod.GET;
        }
    }

    interface Post<Req, Res> extends HttpEndpoint<Req, Res> {
        @Override
        default @NotNull HttpMethod method() {
            return HttpMethod.POST;
        }
    }
}

final class ArmeriaHttpServerFactory implements RpcListenerFactory<ArmeriaHttpServer, HttpEndpoint<?, ?>> {

    @Override
    public @NotNull ArmeriaHttpServer create() {
        return new ArmeriaHttpServer();
    }
}

final class ArmeriaHttpServer implements HttpServer<ArmeriaHttpServer, HttpEndpoint<?, ?>> {

    @Override
    public @NotNull <Req, Res> ArmeriaHttpServer registerEndpoint(@NotNull HttpEndpoint<?, ?> endpoint) {
        return this;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}

