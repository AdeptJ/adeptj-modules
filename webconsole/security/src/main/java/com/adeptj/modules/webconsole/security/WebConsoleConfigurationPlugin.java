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

package com.adeptj.modules.webconsole.security;

import com.adeptj.modules.commons.utils.annotation.ConfigurationPluginProperties;
import com.adeptj.runtime.tools.OSGiConsolePasswordVault;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Component;

import java.util.Dictionary;

import static com.adeptj.modules.webconsole.security.WebConsoleConfigurationPlugin.OSGI_MGR_PID;

/**
 * This {@link ConfigurationPlugin} targets Felix OsgiManager to get the password property.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConfigurationPluginProperties(cm_target = OSGI_MGR_PID, service_cmRanking = 100)
@Component()
public class WebConsoleConfigurationPlugin implements ConfigurationPlugin {

    private static final String CFG_PWD = "password";

    static final String OSGI_MGR_PID = "org.apache.felix.webconsole.internal.servlet.OsgiManager";

    @Override
    public void modifyConfiguration(ServiceReference<?> reference, Dictionary<String, Object> properties) {
        OSGiConsolePasswordVault.getInstance().setPassword((String) properties.get(CFG_PWD));
    }
}
