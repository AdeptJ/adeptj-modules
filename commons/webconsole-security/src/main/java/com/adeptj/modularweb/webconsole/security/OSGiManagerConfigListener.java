/** 
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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
package com.adeptj.modularweb.webconsole.security;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGiManagerConfigListener.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Service(ConfigurationListener.class)
@Component(immediate = true)
public class OSGiManagerConfigListener implements ConfigurationListener {

	private static final String WEBCONSOLE_PWD_UPDATE_AWARE_CLASS = "com.adeptj.runtime.osgi.WebConsolePasswordUpdateAware";

	private static final String OSGI_MGR_PID = "org.apache.felix.webconsole.internal.servlet.OsgiManager";

	private static final Logger LOGGER = LoggerFactory.getLogger(OSGiManagerConfigListener.class);

	@Reference
	private ConfigurationAdmin configAdmin;

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		switch (event.getType()) {
		case ConfigurationEvent.CM_DELETED:
			String deletedPid = event.getPid();
			if (OSGI_MGR_PID.equals(deletedPid)) {
				try {
					LOGGER.info("Deleting pid: [{}]", deletedPid);
					Class<?> klazz = this.passwordUpdateAwareClass();
					Object updateAware = klazz.getMethod("getInstance", (Class[]) null).invoke(null, (Object[]) null);
					klazz.getMethod("setPassword", char[].class).invoke(updateAware, (char[]) null);
				} catch (Exception ex) {
					LOGGER.error("Exception!!", ex);
				}
			}
			break;
		case ConfigurationEvent.CM_UPDATED:
			String updatedPid = event.getPid();
			if (OSGI_MGR_PID.equals(updatedPid)) {
				LOGGER.debug("Handling Configuration update event for pid: [{}]", updatedPid);
				this.handleOSGiManagerPwd(updatedPid);
			}
			break;
		default:
		}
	}

	@Activate
	protected void activate(BundleContext context) {
		this.handleOSGiManagerPwd(OSGI_MGR_PID);
	}

	private void handleOSGiManagerPwd(String pid) {
		Dictionary<String, Object> configs;
		try {
			Configuration cfg = this.configAdmin.getConfiguration(pid, null);
			if (cfg == null) {
				LOGGER.warn("Configuration doesn't exist for pid: [{}]", pid);
			} else if ((configs = cfg.getProperties()) != null && configs.get("password") != null) {
				Class<?> klazz = this.passwordUpdateAwareClass();
				Object updateAware = klazz.getMethod("getInstance", (Class[]) null).invoke(null, (Object[]) null);
				klazz.getMethod("setPassword", char[].class).invoke(updateAware, ((String) configs.get("password")).toCharArray());
			}
		} catch (Exception ex) {
			LOGGER.error("Exception!!", ex);
		}
	}
	
	private Class<?> passwordUpdateAwareClass() throws ClassNotFoundException {
		// Load from Application class loader which in fact is the parent of OSGi Framework.
		return this.getClass().getClassLoader().getParent().loadClass(WEBCONSOLE_PWD_UPDATE_AWARE_CLASS);
	}
}
