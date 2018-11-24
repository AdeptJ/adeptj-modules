package com.adeptj.modules.mvc;

public final class Template {

    private final String name;

    private final TemplateContext templateContext;

    private Template(String name, TemplateContext templateContext) {
        this.name = name;
        this.templateContext = templateContext;
    }

    public String getName() {
        return name;
    }

    public TemplateContext getTemplateContext() {
        return templateContext;
    }

    public static Template using(String name, TemplateContext templateContext) {
        return new Template(name, templateContext);
    }
}
