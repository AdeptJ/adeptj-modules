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

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JaxRSAuthRepository.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true, service = { JaxRSAuthRepository.class, ConfigurationListener.class })
public class JaxRSAuthRepository implements ConfigurationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthRepository.class);

    private static final String JAX_RS_AUTH_FACTORY_PID = "com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory";

    private static final String JAX_RS_AUTH_SERVICE_FACTORY_PID_FILTER = "(service.factoryPid=com.adeptj.modules.jaxrs.base.JaxRSAuthConfigFactory.factory)";

    private static final String UTF8 = "UTF-8";

    @Reference
    private ConfigurationAdmin configAdmin;

    private Map<String, JaxRSAuthConfig> configs = new ConcurrentHashMap<>();

    public JaxRSAuthConfig getAuthConfig(String subject) {
        return this.configs.get(subject);
    }

    @Override
    public void configurationEvent(ConfigurationEvent event) {
        try {
            switch (event.getType()) {
                case ConfigurationEvent.CM_UPDATED:
                    if (JAX_RS_AUTH_FACTORY_PID.equals(event.getFactoryPid())) {
                        LOGGER.info("Factory Configuration PID: {}", event.getPid());
                        JaxRSAuthConfig authConfig = this.toJaxRSAuthConfig(this.configAdmin.getConfiguration(event.getPid()));
                        this.configs.put(authConfig.getSubject(), authConfig);
                    }
                    break;
                case ConfigurationEvent.CM_DELETED:
                    if (JAX_RS_AUTH_FACTORY_PID.equals(event.getFactoryPid())) {
                        LOGGER.info("Factory Configuration PID: {}", event.getPid());
                        this.configs.remove((String) this.configAdmin.getConfiguration(event.getPid()).getProperties().get("subject"));
                    }
                    break;
                default:
                    LOGGER.info("Ignoring ConfigurationEvent: {}", event.getType());
            }
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception!!", ex);
        }
    }

    // LifeCycle methods.

    @Activate
    protected void activate() {
        try {
            // Collect all the factory configs of JaxRSAuthConfigFactory and create a mapping of subject to JaxRSAuthConfig object.
            Configuration[] configs = this.configAdmin.listConfigurations(JAX_RS_AUTH_SERVICE_FACTORY_PID_FILTER);
            Optional.ofNullable(configs).ifPresent(cfgs -> Arrays.stream(cfgs).forEach(cfg -> {
                LOGGER.info("Configuration for Factory PID: {}", cfg.getFactoryPid());
                JaxRSAuthConfig authConfig = this.toJaxRSAuthConfig(cfg);
                this.configs.put(authConfig.getSubject(), authConfig);
            }));
        } catch (Exception ex) { // NOSONAR
            LOGGER.error("Exception!!", ex);
        }
    }

    private JaxRSAuthConfig toJaxRSAuthConfig(Configuration configuration) {
        Dictionary<String, Object> properties = configuration.getProperties();
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

}
