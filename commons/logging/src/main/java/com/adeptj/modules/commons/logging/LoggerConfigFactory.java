package com.adeptj.modules.commons.logging;

import ch.qos.logback.classic.Logger;
import com.adeptj.runtime.tools.LogbackUtil;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Dictionary;

import static ch.qos.logback.classic.Level.toLevel;
import static com.adeptj.modules.commons.logging.LoggerConfigFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

/**
 * LoggerConfigFactory
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = LoggerConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + "=" + COMPONENT_NAME,
        configurationPolicy = IGNORE
)
public class LoggerConfigFactory implements ManagedServiceFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.commons.logging.LoggerConfigFactory.factory";

    private static final String FACTORY_NAME = "AdeptJ Logback LoggerConfigFactory";

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        String level = (String) properties.get("level");
        String[] names = (String[]) properties.get("names");
        Logger logger = LogbackUtil.INSTANCE.getLoggerContext().getLogger(names[0]);
        logger.setLevel(toLevel(level));
        logger.setAdditive((Boolean) properties.get("additivity"));
        LogbackUtil.INSTANCE.getAppenders().forEach(logger::addAppender);
    }

    @Override
    public void deleted(String pid) {

    }
}
