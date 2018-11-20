package com.adeptj.modules.mvc;

public class Template {

    private TemplateContext templateContext;

    private Template(TemplateContext templateContext) {
        this.templateContext = templateContext;
    }

    public TemplateContext getTemplateContext() {
        return templateContext;
    }

    public static Template using(TemplateContext templateContext) {
        return new Template(templateContext);
    }
}
