package com.adeptj.modules.security.core.credential;

import org.osgi.annotation.versioning.ProviderType;

import jakarta.servlet.http.HttpServletRequest;

@ProviderType
public interface CredentialResolver {

    Credential resolve(HttpServletRequest request);
}
