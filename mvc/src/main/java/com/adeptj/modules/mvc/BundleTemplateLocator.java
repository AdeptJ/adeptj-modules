package com.adeptj.modules.mvc;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.locator.PathTemplateLocator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;

import static com.adeptj.modules.mvc.TemplateEngineConstants.TEMPLATE_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BundleTemplateLocator extends PathTemplateLocator<String> implements BundleTrackerCustomizer<List<TemplateEntry>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<Long, List<TemplateEntry>> bundleTemplates;

    private final DelegatingResourceBundleHelper resourceBundleHelper;

    public BundleTemplateLocator(int priority, String rootPath, String suffix) {
        super(priority, rootPath, suffix);
        this.bundleTemplates = new HashMap<>();
        this.resourceBundleHelper = new DelegatingResourceBundleHelper();
    }

    public DelegatingResourceBundleHelper getResourceBundleHelper() {
        return resourceBundleHelper;
    }

    // <------------------------ PathTemplateLocator ---------------------->

    @Override
    protected String constructVirtualPath(String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getAllIdentifiers() {
        return Collections.emptySet();
    }

    @Override
    public Reader locate(String name) {
        for (Map.Entry<Long, List<TemplateEntry>> entry : this.bundleTemplates.entrySet()) {
            for (TemplateEntry templateEntry : entry.getValue()) {
                if (StringUtils.endsWith(templateEntry.getPath(), name + "." + this.getSuffix())) {
                    try {
                        URL template = templateEntry.getTemplate();
                        Reader reader = new InputStreamReader(template.openStream(), this.getDefaultFileEncoding());
                        LOGGER.debug("Template {} located: {}", name, template);
                        return reader;
                    } catch (IOException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return null;
    }

    // <------------------------ BundleTrackerCustomizer ---------------------->


    @Override
    public List<TemplateEntry> addingBundle(Bundle bundle, BundleEvent event) {
        String templatesLocation = bundle.getHeaders().get(TEMPLATE_HEADER);
        if (StringUtils.isNotEmpty(templatesLocation)) {
            LOGGER.info("Locating Mustache templates from {}", templatesLocation);
            Enumeration<URL> templates = bundle.findEntries(templatesLocation, "*.html", true);
            List<TemplateEntry> templateEntries = new ArrayList<>();
            while (templates.hasMoreElements()) {
                templateEntries.add(new TemplateEntry(templates.nextElement()));
            }
            this.bundleTemplates.put(bundle.getBundleId(), templateEntries);
            Enumeration<URL> resourceBundles = bundle.findEntries(templatesLocation, "*.properties", true);
            while (resourceBundles.hasMoreElements()) {
                InputStreamReader reader = null;
                try {
                    reader = new InputStreamReader(resourceBundles.nextElement().openStream(), UTF_8);
                    this.resourceBundleHelper.addResourceBundle(new PropertyResourceBundle(reader));
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
            return templateEntries;
        }

        return null;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, List<TemplateEntry> templateEntries) {
        // NOP
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, List<TemplateEntry> templateEntries) {
        this.bundleTemplates.remove(bundle.getBundleId()).clear();
        templateEntries.clear();
    }
}
