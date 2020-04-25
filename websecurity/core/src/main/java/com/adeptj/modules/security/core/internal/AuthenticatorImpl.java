package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.security.core.Authenticator;
import com.adeptj.modules.security.core.credential.BearerTokenCredential;
import com.adeptj.modules.security.core.credential.Credential;
import com.adeptj.modules.security.core.credential.CredentialResolver;
import com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome;
import com.adeptj.modules.security.core.identitystore.IdentityStore;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.security.core.SecurityConstants.ATTRIBUTE_TOKEN_CREDENTIAL;
import static com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome.INVALID_OUTCOME;
import static com.adeptj.modules.security.core.identitystore.CredentialValidationOutcome.NOT_VALIDATED_OUTCOME;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Designate(ocd = AuthenticatorConfig.class)
@Component(immediate = true)
public class AuthenticatorImpl implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CredentialResolver credentialResolver;

    private final boolean securityDisabled;

    private final List<String> securityDisabledPaths;

    private final List<IdentityStore> identityStores;

    @Activate
    public AuthenticatorImpl(@Reference CredentialResolver credentialResolver, AuthenticatorConfig config) {
        this.credentialResolver = credentialResolver;
        this.securityDisabled = config.disable_security();
        this.securityDisabledPaths = new ArrayList<>(Arrays.asList(config.security_disabled_paths()));
        this.identityStores = new CopyOnWriteArrayList<>();
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request) {
        if (this.securityDisabled || this.securityDisabledPaths.contains(request.getRequestURI())) {
            return true;
        }
        Credential credential = this.credentialResolver.resolve(request);
        if (credential instanceof BearerTokenCredential) {
            request.setAttribute(ATTRIBUTE_TOKEN_CREDENTIAL, credential);
            return true;
        }
        return false;
    }

    @Override
    public void finishSecurity(@NotNull HttpServletRequest request) {
        request.removeAttribute(ATTRIBUTE_TOKEN_CREDENTIAL);
    }

    @Override
    public CredentialValidationOutcome login(HttpServletRequest request, HttpServletResponse response) {
        Credential credential = this.credentialResolver.resolve(request);
        if (credential == null) {
            return NOT_VALIDATED_OUTCOME;
        }
        CredentialValidationOutcome outcome = this.identityStores.stream()
                .sorted(Comparator.comparingInt(IdentityStore::priority).reversed())
                .filter(store -> store.canValidate(credential))
                .map(store -> store.validate(credential))
                .findFirst()
                .orElse(INVALID_OUTCOME);
        credential.clear();
        return outcome;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

    }

    // <<-------------------------------------- OSGi INTERNAL -------------------------------------->>

    @Reference(service = IdentityStore.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindIdentityStore(IdentityStore identityStore) {
        this.identityStores.add(identityStore);
    }

    protected void unbindIdentityStore(IdentityStore identityStore) {
        this.identityStores.remove(identityStore);
    }
}
