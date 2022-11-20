/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.jaxrs.resteasy.internal;

import org.osgi.service.component.annotations.ComponentPropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.RESTEASY_DISPATCHER_SERVLET_PATH;
import static com.adeptj.modules.jaxrs.resteasy.internal.ResteasyConstants.VALUE_TRUE;
import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;

/**
 * {@link ComponentPropertyType} for RESTEasy's mapping prefix property.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ResteasyServletInitParameters {

    /**
     * Prefix for the property name. This value is prepended to each property name.
     */
    String PREFIX_ = HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + "resteasy."; // NOSONAR

    String servlet_mapping_prefix() default RESTEASY_DISPATCHER_SERVLET_PATH; // NOSONAR

    String role_based_security() default VALUE_TRUE; // NOSONAR

    String allowGzip() default VALUE_TRUE; // NOSONAR
}
