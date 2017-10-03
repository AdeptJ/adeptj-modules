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

package com.adeptj.modules.commons.logging.internal;

import com.adeptj.modules.commons.logging.LoggingConfig;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

import static com.adeptj.modules.commons.logging.internal.LoggingConfigFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * Configuration Factory for AdeptJ Logging mechanism.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LoggingConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class LoggingConfigFactory implements ManagedServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConfigFactory.class);

    private static final String FACTORY_NAME = "AdeptJ Logging LoggingConfigFactory";

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.logging.LoggingConfigFactory.factory";

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        LOGGER.info("LoggingConfig: {}", properties);
    }

    @Override
    public void deleted(String pid) {

    }
}
