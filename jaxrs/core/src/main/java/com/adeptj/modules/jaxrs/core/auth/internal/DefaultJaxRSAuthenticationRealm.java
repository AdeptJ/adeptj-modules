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

import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationConfig;
import com.adeptj.modules.jaxrs.core.auth.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.auth.api.JaxRSAuthenticationRealm;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.jaxrs.core.auth.internal.DefaultJaxRSAuthenticationRealm.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Default implementation of JaxRSAuthenticationRealm, this is also a {@link ManagedServiceFactory} for
 * creating {@link JaxRSAuthenticationInfo} instances which are stored in a map for querying purpose
 * in case no other implementation of JaxRSAuthenticationRealm is found.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = JaxRSAuthenticationConfig.class, factory = true)
@Component(
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class DefaultJaxRSAuthenticationRealm implements JaxRSAuthenticationRealm, ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJaxRSAuthenticationRealm.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfoFactory.factory";

    private static final String FACTORY_NAME = "AdeptJ JAX-RS AuthenticationInfo Factory";

    private static final String USERNAME_NULL_MSG = "Username can't ne null!!";

    private static final String PWD_NULL_MSG = "Password can't ne null!!";

    private static final String KEY_USERNAME = "username";

    private static final String KEY_PWD = "password";

    private Map<String, JaxRSAuthenticationInfo> authInfoMap = new ConcurrentHashMap<>();

    private Map<String, String> pidVsUserMappings = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public int priority() {
        return -1; // default realm has least priority.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JaxRSAuthenticationInfo getAuthenticationInfo(String username, String password) {
        JaxRSAuthenticationInfo authInfo = this.authInfoMap.get(username);
        return authInfo != null
                && StringUtils.equals(authInfo.getUsername(), username)
                && Arrays.equals(password.toCharArray(), authInfo.getPassword())
                ? authInfo : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String username = Objects.requireNonNull((String) properties.get(KEY_USERNAME), USERNAME_NULL_MSG);
        String password = Objects.requireNonNull((String) properties.get(KEY_PWD), PWD_NULL_MSG);
        LOGGER.info("Creating JaxRSAuthenticationInfo for User: [{}]", username);
        if (this.pidVsUserMappings.containsKey(pid)) {
            // This is an update
            this.pidVsUserMappings.put(pid, username);
            this.authInfoMap.put(username, new JaxRSAuthenticationInfo(username, password.toCharArray()));
        } else if (!this.pidVsUserMappings.containsKey(pid) && this.pidVsUserMappings.containsValue(username)) {
            LOGGER.warn("User: [{}] already present, ignoring this config!!");
            throw new ConfigurationException(KEY_USERNAME, "User already present!!");
        } else if (!this.pidVsUserMappings.containsKey(pid) && !this.pidVsUserMappings.containsValue(username)) {
            this.pidVsUserMappings.put(pid, username);
            this.authInfoMap.put(username, new JaxRSAuthenticationInfo(username, password.toCharArray()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleted(String pid) {
        Optional.ofNullable(this.pidVsUserMappings.remove(pid)).ifPresent(username -> {
            LOGGER.info("JaxRSAuthenticationInfo removed for User: [{}]", username);
            this.authInfoMap.remove(username);
        });
    }

}
