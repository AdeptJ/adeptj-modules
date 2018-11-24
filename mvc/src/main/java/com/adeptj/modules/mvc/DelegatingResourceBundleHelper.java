package com.adeptj.modules.mvc;

import org.trimou.handlebars.BasicValueHelper;
import org.trimou.handlebars.Options;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static org.trimou.util.Arrays.EMPTY_OBJECT_ARRAY;

public class DelegatingResourceBundleHelper extends BasicValueHelper {

    private final List<ResourceBundleWrapper> resourceBundleWrappers;

    DelegatingResourceBundleHelper() {
        this.resourceBundleWrappers = new ArrayList<>();
    }

    void addResourceBundleWrapper(ResourceBundleWrapper wrapper) {
        this.resourceBundleWrappers.add(wrapper);
    }

    void removeResourceBundleWrapper(long bundleId) {
        this.resourceBundleWrappers.removeIf(wrapper -> wrapper.getBundleId() == bundleId);
    }

    @Override
    public void execute(Options options) {
        List<Object> params = options.getParameters();
        String key = params.get(0).toString();
        for (ResourceBundleWrapper wrapper : this.resourceBundleWrappers) {
            if (wrapper.getResourceBundle().containsKey(key)) {
                append(options, MessageFormat.format(wrapper.getResourceBundle().getString(key),
                        params.size() > 1 ? params.subList(1, params.size()).toArray() : EMPTY_OBJECT_ARRAY));
                break;
            }
        }
    }
}
