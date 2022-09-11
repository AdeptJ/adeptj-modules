package com.adeptj.modules.restclient.core;

import com.adeptj.modules.restclient.core.plugin.AuthorizationHeaderPlugin;
import com.adeptj.modules.restclient.core.util.AntPathMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractRestClient implements RestClient {

    public <T, R> void ensureHttpMethod(@NotNull ClientRequest<T, R> request, HttpMethod method) {
        if (request.getMethod() == null) {
            request.setMethod(method);
        }
    }

    public AuthorizationHeaderPlugin resolveAuthorizationHeaderPlugin(List<AuthorizationHeaderPlugin> plugins, String path) {
        AntPathMatcher matcher = AntPathMatcher.builder().build();
        for (AuthorizationHeaderPlugin plugin : plugins) {
            for (String pattern : plugin.getPathPatterns()) {
                if (matcher.isMatch(pattern, path)) {
                    return plugin;
                }
            }
        }
        return null;
    }
}
