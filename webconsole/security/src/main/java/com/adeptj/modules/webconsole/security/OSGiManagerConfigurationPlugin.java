package com.adeptj.modules.webconsole.security;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Dictionary;

import static com.adeptj.modules.webconsole.security.OSGiManagerConfigurationPlugin.OSGI_MGR_PID;
import static org.osgi.service.cm.ConfigurationPlugin.CM_RANKING;
import static org.osgi.service.cm.ConfigurationPlugin.CM_TARGET;

@Component(property = {
        CM_TARGET + "=" + OSGI_MGR_PID,
        CM_RANKING + ":Integer=100"
})
public class OSGiManagerConfigurationPlugin implements ConfigurationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String OSGI_MGR_PID = "org.apache.felix.webconsole.internal.servlet.OsgiManager";

    @Override
    public void modifyConfiguration(ServiceReference<?> reference, Dictionary<String, Object> properties) {
        LOGGER.info("Dictionary: {}", properties);
    }
}
