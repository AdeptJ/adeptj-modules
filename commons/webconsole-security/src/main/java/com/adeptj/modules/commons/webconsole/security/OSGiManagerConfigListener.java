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

package com.adeptj.modules.commons.webconsole.security;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Dictionary;

import static java.lang.invoke.MethodType.methodType;

/**
 * ConfigurationListener for Felix {@link org.apache.felix.webconsole.internal.servlet.OsgiManager}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true)
public class OSGiManagerConfigListener implements ConfigurationListener {

	private static final String CFG_PWD = "password";

	private static final String METHOD_SET_PASSWORD = "setPassword";

	private static final String METHOD_GET_INSTANCE = "getInstance";

	private static final String WEBCONSOLE_PWD_UPDATE_AWARE_CLASS = "com.adeptj.runtime.osgi.WebConsolePasswordUpdateAware";

	private static final String OSGI_MGR_PID = "org.apache.felix.webconsole.internal.servlet.OsgiManager";

	private static final Logger LOGGER = LoggerFactory.getLogger(OSGiManagerConfigListener.class);

	@Reference
	private ConfigurationAdmin configAdmin;
	
	private MethodHandle setPwdMethodHandle;

	@Override
	public void configurationEvent(ConfigurationEvent event) {
		switch (event.getType()) {
		case ConfigurationEvent.CM_DELETED:
			String deletedPid = event.getPid();
			if (OSGI_MGR_PID.equals(deletedPid)) {
				try {
					LOGGER.info("Deleting pid: [{}]", deletedPid);
					this.setPwdMethodHandle.invoke((char[]) null);
				} catch (Throwable th) { // NOSONAR
					LOGGER.error("Exception!!", th);
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
	protected void activate(BundleContext context) throws ClassNotFoundException {
		this.initSetPwdMethodHandle();
		this.handleOSGiManagerPwd(OSGI_MGR_PID);
	}

	private void handleOSGiManagerPwd(String pid) {
		Dictionary<String, Object> configs;
		try {
			Configuration cfg = this.configAdmin.getConfiguration(pid, null);
			if (cfg == null) {
				LOGGER.warn("Configuration doesn't exist for pid: [{}]", pid);
			} else if ((configs = cfg.getProperties()) != null && configs.get(CFG_PWD) != null) {
				this.setPwdMethodHandle.invoke(((String) configs.get(CFG_PWD)).toCharArray());
			}
		} catch (Throwable th) { // NOSONAR
			LOGGER.error("Exception!!", th);
		}
	}
	
	private void initSetPwdMethodHandle() {
		if (this.setPwdMethodHandle == null) {
			try {
				// Load from Application class loader which in fact is the parent of OSGi Framework.
				Class<?> cls = this.getClass()
                        .getClassLoader()
                        .getParent()
                        .loadClass(WEBCONSOLE_PWD_UPDATE_AWARE_CLASS);
				MethodHandles.Lookup lookup = MethodHandles.lookup();
				this.setPwdMethodHandle = lookup.findVirtual(cls, METHOD_SET_PASSWORD, methodType(void.class, char[].class))
						.bindTo(lookup.findStatic(cls, METHOD_GET_INSTANCE, methodType(cls)).invoke());
			} catch (Throwable th) { // NOSONAR
				LOGGER.error("Exception!!", th);
			}	
		}
	}
}
