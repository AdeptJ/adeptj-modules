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

import com.adeptj.runtime.tools.OSGiConsolePasswordVault;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;

import static org.osgi.service.cm.ConfigurationEvent.CM_DELETED;
import static org.osgi.service.cm.ConfigurationEvent.CM_UPDATED;

/**
 * ConfigurationListener for Felix OsgiManager
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, property = "adeptj.osgi.config.listener=OSGiManager")
public class OSGiManagerConfigListener implements ConfigurationListener {

    private static final String CFG_PWD = "password";

    private static final String OSGI_MGR_PID = "org.apache.felix.webconsole.internal.servlet.OsgiManager";

    private static final Logger LOGGER = LoggerFactory.getLogger(OSGiManagerConfigListener.class);

    @Reference
    private ConfigurationAdmin configAdmin;

    @Override
    public void configurationEvent(ConfigurationEvent event) {
        if (StringUtils.equals(OSGI_MGR_PID, event.getPid())) {
            switch (event.getType()) {
                case CM_DELETED:
                    OSGiConsolePasswordVault.getInstance().setPassword(null);
                    break;
                case CM_UPDATED:
                    this.handleOSGiManagerPwd(event.getPid());
                    break;
                default:
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignoring the ConfigurationEvent type: [{}]", event.getType());
                    }
                    break;
            }
        }
    }

    // Lifecycle methods

    @Activate
    protected void start(BundleContext context) {
        this.handleOSGiManagerPwd(OSGI_MGR_PID);
    }

    private void handleOSGiManagerPwd(String pid) {
        try {
            Dictionary<String, Object> properties = this.configAdmin.getConfiguration(pid, null).getProperties();
            if (properties == null) {
                return;
            }
            OSGiConsolePasswordVault.getInstance().setPassword(((String) properties.get(CFG_PWD)));
            LOGGER.info("OSGi Web Console password set successfully!!");
        } catch (IOException ex) {
            LOGGER.error("IOException!!", ex);
        }
    }
}
