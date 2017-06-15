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
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JaxRSAuthConfigFactory.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = JaxRSAuthConfigOCD.class)
@Component(name = JaxRSAuthConfigFactory.NAME, property = JaxRSAuthConfigFactory.SERVICE_PID, configurationPolicy = ConfigurationPolicy.IGNORE)
public class JaxRSAuthConfigFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthConfigFactory.class);

    public static final String NAME = "com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory";

    public static final String SERVICE_PID = "service.pid=com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory";

    private Map<String, JaxRSAuthConfig> mappings = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        LOGGER.info("pid: [{}]", pid);
        String subject = (String) properties.get("subject");
        String password = (String) properties.get("password");
        String signingKey = (String) properties.get("signingKey");
        JaxRSAuthConfig.Builder builder = new JaxRSAuthConfig.Builder();
        builder.subject(subject).password(password).signingKey(signingKey).origins(new ArrayList<>(Arrays.asList((String[]) properties.get("origins"))))
                .userAgents(new ArrayList<>(Arrays.asList((String[]) properties.get("userAgents"))));
        JaxRSAuthConfig config = builder.build();
        this.mappings.put(pid, config);
        try {
            JaxRSAuthConfigProvider.INSTANCE.addJaxRSAuthConfig(config);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void deleted(String pid) {
        JaxRSAuthConfig config = this.mappings.remove(pid);
        Optional.ofNullable(config).ifPresent(consumer -> JaxRSAuthConfigProvider.INSTANCE.deleteJaxRSAuthConfig(config.getSubject()));
    }
}
