package com.adeptj.modules.security.core.credential;

import org.osgi.annotation.versioning.ProviderType;

import javax.servlet.http.HttpServletRequest;

@ProviderType
public interface CredentialProvider {

    Credential getCredential(HttpServletRequest request);
}
