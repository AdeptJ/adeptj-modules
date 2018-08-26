package com.adeptj.modules.jaxrs.core;

import org.osgi.service.component.annotations.ComponentPropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JaxRSResource {

    /**
     * Prefix for the property name. This value is prepended to each property name.
     */
    String PREFIX_ = "osgi.jaxrs.resource."; // NOSONAR

    // This is a marker annotation.

    String name();
}
