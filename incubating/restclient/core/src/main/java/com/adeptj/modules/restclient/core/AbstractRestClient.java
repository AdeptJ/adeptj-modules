package com.adeptj.modules.restclient.core;

import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import com.adeptj.modules.restclient.core.util.AntPathMatcher;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.restclient.core.HttpMethod.DELETE;
import static com.adeptj.modules.restclient.core.HttpMethod.GET;
import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static com.adeptj.modules.restclient.core.HttpMethod.PUT;

public abstract class AbstractRestClient implements RestClient {

    protected final boolean debugRequest;

    protected final String mdcReqIdAttrName;

    protected final List<AuthorizationHeaderPlugin> authorizationHeaderPlugins;

    public AbstractRestClient(boolean debugRequest, String mdcReqIdAttrName) {
        this.debugRequest = debugRequest;
        this.mdcReqIdAttrName = mdcReqIdAttrName;
        this.authorizationHeaderPlugins = new CopyOnWriteArrayList<>();
    }

    protected <T, R> void ensureHttpMethod(@NotNull ClientRequest<T, R> request, HttpMethod method) {
        if (request.getMethod() == null) {
            request.setMethod(method);
        }
    }

    @Override
    public <T, R> ClientResponse<R> GET(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, GET);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> POST(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, POST);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> PUT(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, PUT);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(@NotNull ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, DELETE);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(@NotNull ClientRequest<T, R> request) {
        if (request.getMethod() == null) {
            throw new IllegalStateException("No HttpMethod set in the ClientRequest!!");
        }
        return this.doExecuteRequest(request);
    }

    protected String getAuthorizationHeaderValue(String reqPath) {
        // Create a temp var because the service is dynamic.
        List<AuthorizationHeaderPlugin> plugins = this.authorizationHeaderPlugins;
        if (plugins.isEmpty()) {
            return null;
        }
        String authorizationHeaderValue = null;
        AntPathMatcher matcher = AntPathMatcher.builder().build();
        for (AuthorizationHeaderPlugin plugin : plugins) {
            for (String pattern : plugin.getPathPatterns()) {
                if (matcher.isMatch(pattern, reqPath)) {
                    this.getLogger().info("Authorization header added to request [{}] by plugin [{}]", reqPath, plugin);
                    authorizationHeaderValue = plugin.getType() + " " + plugin.getValue();
                    break;
                }
            }
        }
        return authorizationHeaderValue;
    }

    protected void doBindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        this.getLogger().info("Binding AuthorizationHeaderPlugin: {}", plugin);
        this.authorizationHeaderPlugins.add(plugin);
    }

    protected void doUnbindAuthorizationHeaderPlugin(AuthorizationHeaderPlugin plugin) {
        if (this.authorizationHeaderPlugins.remove(plugin)) {
            this.getLogger().info("Unbind AuthorizationHeaderPlugin: {}", plugin);
        }
    }

    protected abstract <T, R> @NotNull ClientResponse<R> doExecuteRequest(ClientRequest<T, R> request);

    protected abstract Logger getLogger();
}
