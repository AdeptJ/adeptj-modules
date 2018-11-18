package com.adeptj.modules.mvc;

import org.osgi.framework.Bundle;
import org.trimou.engine.locator.AbstractTemplateLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Set;

public class BundleClassPathTemplateLocator extends AbstractTemplateLocator {

    private final Bundle bundle;

    public BundleClassPathTemplateLocator(Bundle bundle) {
        super(5000);
        this.bundle = bundle;
    }

    @Override
    public Set<String> getAllIdentifiers() {
        return null;
    }

    @Override
    public Reader locate(String name) {
        URL resource = bundle.getResource(name);
        try {
            return new BufferedReader(new InputStreamReader(resource.openStream()));
        } catch (IOException e) {
        }
        return null;
    }
}
