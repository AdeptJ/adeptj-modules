package com.adeptj.modules.security.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authenticator
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface Authenticator {

    String DEFAULT_SERVLET_CONTEXT_NAME = "DefaultServletContext";

    boolean handleSecurity(HttpServletRequest req, HttpServletResponse resp);
}
