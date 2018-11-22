package com.adeptj.modules.mvc;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ TemplateEngine Configuration",
        description = "AdeptJ Mustache TemplateEngine Configuration"
)
public @interface TemplateEngineConfig {

    @AttributeDefinition(name = "Bundle TemplateLocator Priority")
    int bundleTemplateLocatorPriority() default 5000;

    @AttributeDefinition(name = "Bundle TemplateLocator Priority")
    int classpathTemplateLocatorPriority() default 4000;

    @AttributeDefinition(name = "Bundle Template Prefix")
    String bundleTemplatePrefix() default "WEB-INF/views/";

    @AttributeDefinition(name = "Classpath Template Prefix")
    String classpathTemplatePrefix() default "tools/WEB-INF/views/";

    @AttributeDefinition(name = "Template Suffix")
    String suffix() default "html";

    @AttributeDefinition(name = "Template Start Delimiter")
    String startDelimiter() default "{{";

    @AttributeDefinition(name = "Template End Delimiter")
    String endDelimiter() default "}}";

    @AttributeDefinition(name = "Template ResourceBundle BaseName")
    String resourceBundleBasename();

    @AttributeDefinition(name = "Template Encoding")
    String encoding() default "UTF-8";

    @AttributeDefinition(name = "Template Cache Enabled")
    boolean cacheEnabled();

    @AttributeDefinition(name = "Template Cache Expiration")
    int cacheExpiration() default 3600;
}
