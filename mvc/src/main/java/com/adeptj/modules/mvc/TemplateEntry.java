package com.adeptj.modules.mvc;

import java.net.URL;

public final class TemplateEntry {

    private final String path;

    private final URL template;

    TemplateEntry(URL template) {
        this.path = template.getPath();
        this.template = template;
    }

    public String getPath() {
        return path;
    }

    public URL getTemplate() {
        return template;
    }
}
