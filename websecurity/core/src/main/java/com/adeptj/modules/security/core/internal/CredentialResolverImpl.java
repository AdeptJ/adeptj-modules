package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.security.core.credential.BasicAuthCredential;
import com.adeptj.modules.security.core.credential.BearerTokenCredential;
import com.adeptj.modules.security.core.credential.Credential;
import com.adeptj.modules.security.core.credential.CredentialResolver;
import com.adeptj.modules.security.core.credential.UsernamePasswordCredential;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CredentialResolverImpl implements CredentialResolver {

    @Override
    public Credential resolve(HttpServletRequest request) {
        Credential credential = UsernamePasswordCredential.from(request);
        if (credential == null) {
            credential = BearerTokenCredential.from(request);
        }
        if (credential == null) {
            credential = BasicAuthCredential.from(request);
        }
        return credential;
    }
}
