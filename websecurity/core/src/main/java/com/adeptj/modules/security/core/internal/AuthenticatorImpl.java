package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.security.core.Authenticator;
import com.adeptj.modules.security.core.identitystore.IdentityStore;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component()
public class AuthenticatorImpl implements Authenticator {

    /**
     * As per Felix SCR, dynamic references should be declared as volatile.
     */
    @Reference(service = IdentityStore.class, cardinality = MULTIPLE, policy = DYNAMIC)
    private volatile List<IdentityStore> identityStores;

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
        return this.identityStores.stream()
                .sorted(Comparator.comparingInt(IdentityStore::priority).reversed())
                .map(store -> store.validate(null))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(false);
    }

    @Override
    public void finishSecurity(HttpServletRequest request, HttpServletResponse response) {

    }
}
