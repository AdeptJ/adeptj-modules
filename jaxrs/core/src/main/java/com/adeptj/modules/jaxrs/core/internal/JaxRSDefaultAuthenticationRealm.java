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

package com.adeptj.modules.jaxrs.core.internal;

import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationConfig;
import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.api.JaxRSAuthenticationRealm;
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

import static com.adeptj.modules.jaxrs.core.internal.JaxRSDefaultAuthenticationRealm.COMPONENT_NAME;
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
public class JaxRSDefaultAuthenticationRealm implements JaxRSAuthenticationRealm, ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSDefaultAuthenticationRealm.class);

    static final String COMPONENT_NAME = "com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfoFactory.factory";

    private static final String FACTORY_NAME = "AdeptJ JAX-RS AuthenticationInfo Factory";

    private static final String SUB_NULL_MSG = "Subject can't ne null!!";

    private static final String PWD_NULL_MSG = "Password can't ne null!!";

    private static final String KEY_SUBJECT = "subject";

    private static final String KEY_PWD = "password";

    private Map<String, JaxRSAuthenticationInfo> authenticationInfoMap = new ConcurrentHashMap<>();

    private Map<String, String> pidVsSubjectMappings = new ConcurrentHashMap<>();

    @Override
    public int priority() {
        return 0; // default realm has least priority.
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public JaxRSAuthenticationInfo getAuthenticationInfo(String subject, String password) {
        LOGGER.info("Getting JaxRSAuthenticationInfo for Subject: [{}]", subject);
        JaxRSAuthenticationInfo authenticationInfo = this.authenticationInfoMap.get(subject);
        if (authenticationInfo == null
                || authenticationInfo.getPassword() == null
                || authenticationInfo.getPassword().length == 0) {
            return null;
        }
        if (StringUtils.equals(authenticationInfo.getSubject(), subject)
                && Arrays.equals(password.toCharArray(), authenticationInfo.getPassword())) {
            return authenticationInfo;
        }
        return null;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String subject = Objects.requireNonNull((String) properties.get(KEY_SUBJECT), SUB_NULL_MSG);
        String password = Objects.requireNonNull((String) properties.get(KEY_PWD), PWD_NULL_MSG);
        LOGGER.info("Creating JaxRSAuthenticationInfo for Subject: [{}]", subject);
        if (this.pidVsSubjectMappings.containsKey(pid)) {
            // This is an update
            this.pidVsSubjectMappings.put(pid, subject);
            this.authenticationInfoMap.put(subject, new JaxRSAuthenticationInfo(subject, password.toCharArray()));
        } else if (!this.pidVsSubjectMappings.containsKey(pid) && this.pidVsSubjectMappings.containsValue(subject)) {
            LOGGER.warn("Subject: [{}] already present, ignoring this config!!");
        } else if (!this.pidVsSubjectMappings.containsKey(pid) && !this.pidVsSubjectMappings.containsValue(subject)) {
            this.pidVsSubjectMappings.put(pid, subject);
            this.authenticationInfoMap.put(subject, new JaxRSAuthenticationInfo(subject, password.toCharArray()));
        }
    }

    @Override
    public void deleted(String pid) {
        Optional.ofNullable(this.pidVsSubjectMappings.remove(pid)).ifPresent(subject -> {
            LOGGER.info("JaxRSAuthenticationInfo removed for Subject: [{}]", subject);
            this.authenticationInfoMap.remove(subject);
        });
    }

}
