package com.adeptj.modules.restclient.plugin;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.List;

/**
 * Plugin for injecting the Authorization header in matching request paths.
 * <p>
 * RestClient consumers should implemented this interface if they need seamless injection of Authorization
 * header in matching request paths.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface AuthorizationHeaderPlugin {

    /**
     * Should return the type of authentication scheme such as Bearer or Basic
     *
     * @return authentication scheme such as Bearer or Basic
     */
    @NotNull
    String getType();

    /**
     * Should return the Authorization header value.
     *
     * @return Authorization header value
     */
    @NotNull
    String getValue();

    /**
     * Should return the request path patterns on which the Authorization header to be injected.
     * <p>
     * Example: /api/** or /api/users/* etc.
     * <p>
     * '?' - matches one character
     * '*' - matches zero or more characters
     * '**' - matches zero or more directories in a path
     *
     * @return request path patterns
     */
    @NotNull
    List<String> getPathPatterns();
}
