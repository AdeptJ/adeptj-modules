package com.adeptj.modules.mvc;

import org.osgi.framework.Bundle;
import org.trimou.engine.locator.AbstractTemplateLocator;

import java.io.Reader;
import java.util.Set;

public class BundleClassPathTemplateLocator extends AbstractTemplateLocator {

    private Bundle bundle;

    public BundleClassPathTemplateLocator() {
        super(5000);
    }

    @Override
    public Set<String> getAllIdentifiers() {
        return null;
    }

    @Override
    public Reader locate(String name) {
        return null;
    }
}
