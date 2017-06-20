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
import java.util.Arrays;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.NAME;
import static com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.SERVICE_PID;

/**
 * JaxRSAuthConfigFactory.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = JaxRSAuthConfigOCD.class)
@Component(name = NAME, property = SERVICE_PID, service = {JaxRSAuthConfigFactory.class, ManagedServiceFactory.class},
        configurationPolicy = ConfigurationPolicy.IGNORE)
public class JaxRSAuthConfigFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthConfigFactory.class);

    private static final String UTF8 = "UTF-8";

    static final String NAME = "com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory";

    static final String SERVICE_PID = "service.pid=com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory";

    private Map<String, JaxRSAuthConfig> jaxRSAuthConfigs = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        LOGGER.info("pid: [{}]", pid);
        this.jaxRSAuthConfigs.put(pid, this.toJaxRSAuthConfig(properties));
    }

    private JaxRSAuthConfig toJaxRSAuthConfig(Dictionary<String, ?> properties) {
        return new JaxRSAuthConfig.Builder()
                .subject((String) properties.get("subject"))
                .password((String) properties.get("password"))
                .signingKey(this.encode((String) properties.get("signingKey")))
                .origins(Arrays.asList((String[]) properties.get("origins")))
                .userAgents(Arrays.asList((String[]) properties.get("userAgents")))
                .build();
    }

    private String encode(String signingKey) {
        try {
            return new String(Base64.getEncoder().encode(signingKey.getBytes(UTF8)));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    @Override
    public void deleted(String pid) {
        this.jaxRSAuthConfigs.remove(pid);
    }

    public Map<String, JaxRSAuthConfig> getJaxRSAuthConfigs() {
        return this.jaxRSAuthConfigs;
    }

    public JaxRSAuthConfig getJaxRSAuthConfig(String subject) {
        return this.jaxRSAuthConfigs.get(subject);
    }
}
