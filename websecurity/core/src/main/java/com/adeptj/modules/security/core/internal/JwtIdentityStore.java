package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.security.core.credential.Credential;
import com.adeptj.modules.security.core.credential.TokenCredential;
import com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome;
import com.adeptj.modules.security.core.identitystore.IdentityStore;
import org.osgi.service.component.annotations.Component;

@Component
public class JwtIdentityStore implements IdentityStore {

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public String getName() {
        return "AdeptJ Jwt IdentityStore";
    }

    @Override
    public boolean canValidate(Credential credential) {
        return credential instanceof TokenCredential;
    }

    @Override
    public CredentialValidationOutcome validate(Credential credential) {
        return null;
    }
}
