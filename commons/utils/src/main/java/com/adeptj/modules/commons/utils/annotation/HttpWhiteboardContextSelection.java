package com.adeptj.modules.commons.utils.annotation;

import org.osgi.service.component.annotations.ComponentPropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ComponentPropertyType} for selecting the ServletContextHelper using HTTP_WHITEBOARD_CONTEXT_SELECT property.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ComponentPropertyType
public @interface HttpWhiteboardContextSelection {

    /**
     * Prefix for the property name. This value is prepended to each property name.
     */
    String PREFIX_ = "osgi.http.whiteboard.context.";

    String select();
}
