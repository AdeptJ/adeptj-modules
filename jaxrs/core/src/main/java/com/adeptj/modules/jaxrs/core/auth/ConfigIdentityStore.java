package com.adeptj.modules.jaxrs.core.auth;

import com.adeptj.modules.security.core.credential.Credential;
import com.adeptj.modules.security.core.credential.UsernamePasswordCredential;
import com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome;
import com.adeptj.modules.security.core.identitystore.IdentityStore;
import org.osgi.service.component.annotations.Component;

@Component
public class ConfigIdentityStore implements IdentityStore {

    @Override
    public int priority() {
        return 2000;
    }

    @Override
    public String getName() {
        return "OSGi configuration IdentityStore";
    }

    @Override
    public boolean canValidate(Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    public CredentialValidationOutcome validate(Credential credential) {
        return null;
    }
}
