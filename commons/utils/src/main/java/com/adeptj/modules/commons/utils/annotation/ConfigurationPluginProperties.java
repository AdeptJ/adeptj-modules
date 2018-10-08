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

package com.adeptj.modules.commons.utils.annotation;

import org.osgi.service.component.annotations.ComponentPropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link ComponentPropertyType} for {@link org.osgi.service.cm.ConfigurationPlugin} properties.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ComponentPropertyType
public @interface ConfigurationPluginProperties {

    /**
     * Service PID of the targeted service.
     *
     * @return Service PID of the targeted service.
     */
    String cm_target(); // NOSONAR

    /**
     * A service property to specify the order in which plugins are invoked.
     *
     * @return rank by order of which the {@link org.osgi.service.cm.ConfigurationPlugin} instances will be invoked.
     */
    int service_cmRanking(); // NOSONAR
}
