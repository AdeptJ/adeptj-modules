package com.adeptj.modules.commons.logging;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Dictionary;

import static com.adeptj.modules.commons.logging.LogWriterConfigFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * LoggerConfigFactory
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LogWriterConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class LogWriterConfigFactory implements ManagedServiceFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.logging.LogWriterConfigFactory.factory";

    private static final String FACTORY_NAME = "AdeptJ Logging LogWriterConfigFactory";

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {

    }

    @Override
    public void deleted(String pid) {

    }
}
