package com.adeptj.modules.mvc;

import java.util.ResourceBundle;

final class ResourceBundleWrapper {

    private final ResourceBundle resourceBundle;

    private final long bundleId;

    ResourceBundleWrapper(ResourceBundle resourceBundle, long bundleId) {
        this.resourceBundle = resourceBundle;
        this.bundleId = bundleId;
    }

    ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    long getBundleId() {
        return bundleId;
    }
}
