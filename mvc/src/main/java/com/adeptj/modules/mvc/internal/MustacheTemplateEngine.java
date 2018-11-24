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

package com.adeptj.modules.mvc.internal;

import com.adeptj.modules.commons.utils.TimeUtil;
import com.adeptj.modules.mvc.BundleTemplateLocator;
import com.adeptj.modules.mvc.Template;
import com.adeptj.modules.mvc.TemplateContext;
import com.adeptj.modules.mvc.TemplateEngine;
import com.adeptj.modules.mvc.TemplateEngineConfig;
import com.adeptj.modules.mvc.TemplateProcessingException;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.BundleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.ClassPathTemplateLocator;

import java.lang.invoke.MethodHandles;
import java.util.Hashtable;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_VENDOR;
import static org.trimou.engine.config.EngineConfigurationKey.DEFAULT_FILE_ENCODING;
import static org.trimou.engine.config.EngineConfigurationKey.END_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.START_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_ENABLED;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT;

/**
 * Renders Html Templates using Trimou {@link MustacheEngine}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = TemplateEngineConfig.class)
@Component(immediate = true)
public class MustacheTemplateEngine implements TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String TEMPLATE_ENGINE_INIT_MSG = "MustacheTemplateEngine initialized in [{}] ms!!";

    private static final int SB_CAPACITY = Integer.getInteger("template.builder.capacity", 128);

    private BundleTracker<Object> bundleTracker;

    private ServiceRegistration<MustacheEngine> serviceRegistration;

    private MustacheEngine mustacheEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Template template) {
        TemplateContext context = template.getTemplateContext();
        try {
            Mustache mustache = this.mustacheEngine.getMustache(template.getName());
            if (mustache == null) {
                context.getResponse().sendError(SC_NOT_FOUND);
                return;
            }
            StringBuilder output = new StringBuilder(SB_CAPACITY);
            mustache.render(output, context);
            IOUtils.write(output.toString(), context.getResponse().getWriter());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            context.getRequest().setAttribute(ERROR_EXCEPTION, ex);
            throw new TemplateProcessingException(ex);
        }
    }

    // <-------------------------------------------- OSGi Internal -------------------------------------------->

    @Activate
    public void start(BundleContext context, TemplateEngineConfig config) {
        long startTime = System.nanoTime();
        BundleTemplateLocator bundleTemplateLocator = new BundleTemplateLocator(config.bundleTemplateLocatorPriority(),
                config.bundleTemplatePrefix(),
                config.suffix());
        this.mustacheEngine = MustacheEngineBuilder.newBuilder()
                .setProperty(START_DELIMITER, config.startDelimiter())
                .setProperty(END_DELIMITER, config.endDelimiter())
                .setProperty(DEFAULT_FILE_ENCODING, config.encoding())
                .setProperty(TEMPLATE_CACHE_ENABLED, config.cacheEnabled())
                .setProperty(TEMPLATE_CACHE_EXPIRATION_TIMEOUT, config.cacheExpiration())
                .registerHelper(config.resourceBundleHelperName(), bundleTemplateLocator.getResourceBundleHelper())
                .addTemplateLocator(bundleTemplateLocator)
                .addTemplateLocator(ClassPathTemplateLocator.builder()
                        .setPriority(config.classpathTemplateLocatorPriority())
                        .setRootPath(config.classpathTemplatePrefix())
                        .setSuffix(config.suffix())
                        .setScanClasspath(false)
                        .build())
                .build();
        LOGGER.info(TEMPLATE_ENGINE_INIT_MSG, TimeUtil.elapsedMillis(startTime));
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put(SERVICE_VENDOR, "AdeptJ");
        properties.put(SERVICE_DESCRIPTION, "AdeptJ MustacheEngine Service");
        this.serviceRegistration = context.registerService(MustacheEngine.class, this.mustacheEngine, properties);
        this.bundleTracker = new BundleTracker<>(context, ACTIVE, bundleTemplateLocator);
        this.bundleTracker.open();
    }

    @Deactivate
    public void stop() {
        this.bundleTracker.close();
        this.serviceRegistration.unregister();
        this.mustacheEngine.invalidateTemplateCache();
        this.mustacheEngine = null;
    }
}