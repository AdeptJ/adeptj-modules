package com.adeptj.modules.mvc;

import org.trimou.handlebars.BasicValueHelper;
import org.trimou.handlebars.Options;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.trimou.util.Arrays.EMPTY_OBJECT_ARRAY;

public class DelegatingResourceBundleHelper extends BasicValueHelper {

    private final List<ResourceBundle> resourceBundles;

    DelegatingResourceBundleHelper() {
        this.resourceBundles = new ArrayList<>();
    }

    void addResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundles.add(resourceBundle);
    }

    List<ResourceBundle> getResourceBundles() {
        return resourceBundles;
    }

    @Override
    public void execute(Options options) {
        List<Object> params = options.getParameters();
        String key = params.get(0).toString();
        for (ResourceBundle resourceBundle : this.resourceBundles) {
            if (resourceBundle.containsKey(key)) {
                append(options, MessageFormat.format(resourceBundle.getString(key),
                        params.size() > 1 ? params.subList(1, params.size()).toArray() : EMPTY_OBJECT_ARRAY));
                break;
            }
        }
    }
}
