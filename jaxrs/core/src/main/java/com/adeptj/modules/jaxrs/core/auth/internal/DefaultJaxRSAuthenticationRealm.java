/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.jaxrs.core.auth.internal;

import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationOutcome;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.ROLES;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * Default implementation of JaxRSAuthenticationRealm.
 * <p>
 * Authenticates using the {@link SimpleCredentials} obtained via {@link JaxRSCredentialsFactory}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class DefaultJaxRSAuthenticationRealm implements JaxRSAuthenticationRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<SimpleCredentials> credentials;

    public DefaultJaxRSAuthenticationRealm() {
        this.credentials = new CopyOnWriteArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int priority() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxRSAuthenticationOutcome authenticate(SimpleCredentials credentials) {
        return this.credentials.stream()
                .filter(credentials::equals)
                .map(sc -> {
                    JaxRSAuthenticationOutcome authenticationOutcome = new JaxRSAuthenticationOutcome();
                    if (sc.getRoles() != null) {
                        authenticationOutcome.addAttribute(ROLES, sc.getRoles());
                    }
                    return authenticationOutcome;
                })
                .findFirst()
                .orElse(null);
    }

    // <<------------------------------------------ OSGi INTERNAL -------------------------------------------->>

    @Reference(service = JaxRSCredentialsFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindJaxRSCredentialsFactory(JaxRSCredentialsFactory credentialsFactory) {
        SimpleCredentials simpleCredentials = credentialsFactory.getCredentials();
        LOGGER.info("Creating {}", simpleCredentials);
        if (this.credentials.contains(simpleCredentials)) {
            String username = simpleCredentials.getUsername();
            LOGGER.warn("Username: [{}] already present, ignoring these credentials!!", username);
            throw new IllegalStateException(String.format("Username: [%s] already present, ignoring these credentials!!", username));
        }
        this.credentials.add(simpleCredentials);
    }

    protected void unbindJaxRSCredentialsFactory(JaxRSCredentialsFactory credentialsFactory) {
        SimpleCredentials simpleCredentials = credentialsFactory.getCredentials();
        LOGGER.info("Deleting {}", simpleCredentials);
        this.credentials.remove(simpleCredentials);
    }
}
