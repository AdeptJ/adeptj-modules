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
package com.adeptj.modules.jaxrs.base;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.jaxrs.base.JaxRSAuthenticationInfoFactory.FACTORY_NAME;
import static com.adeptj.modules.jaxrs.base.JaxRSAuthenticationInfoFactory.SERVICE_PID_PROPERTY;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * JaxRSAuthenticationInfoFactory.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = JaxRSAuthenticationConfig.class)
@Component(immediate = true, name = FACTORY_NAME, property = SERVICE_PID_PROPERTY, configurationPolicy = IGNORE,
        service = {JaxRSAuthenticationInfoFactory.class, ManagedServiceFactory.class})
public class JaxRSAuthenticationInfoFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthenticationInfoFactory.class);

    private static final String UTF8 = "UTF-8";

    static final String FACTORY_NAME = "com.adeptj.modules.jaxrs.base.JaxRSAuthenticationInfoFactory.factory";

    static final String SERVICE_PID_PROPERTY = "service.pid=com.adeptj.modules.jaxrs.base.JaxRSAuthenticationInfoFactory.factory";

    private Map<String, JaxRSAuthenticationInfo> authenticationInfoMap = new ConcurrentHashMap<>();

    private Map<String, String> pidVsSubjectMappings = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String subject = (String) properties.get("subject");
        LOGGER.info("Creating JaxRSAuthenticationInfo for Subject: [{}]", subject);
        this.pidVsSubjectMappings.put(pid, subject);
        this.authenticationInfoMap.put(subject, new JaxRSAuthenticationInfo(subject, (String) properties.get("password")));
    }

    @Override
    public void deleted(String pid) {
        Optional.ofNullable(this.pidVsSubjectMappings.remove(pid)).ifPresent(subject -> {
            LOGGER.info("JaxRSAuthenticationInfo removed for Subject: [{}]", subject);
            this.authenticationInfoMap.remove(subject);
        });
    }

    JaxRSAuthenticationInfo getAuthenticationInfo(String subject) {
        return this.authenticationInfoMap.get(subject);
    }
}
