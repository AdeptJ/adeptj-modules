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

package com.adeptj.modules.data.jpa;

import org.osgi.annotation.versioning.ConsumerType;

import java.util.Collections;
import java.util.Map;

/**
 * Service implementation must have the visibility to the entity classes and persistence.xml/orm.xml otherwise
 * EclipseLink may not be able to create the EntityManagerFactory.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ConsumerType
public interface PersistenceInfoProvider {

    /**
     * Implementor must return the JPA persistence unit name exactly defined in persistence.xml.
     *
     * @return a non null persistence unit name.
     */
    String getPersistenceUnitName();

    /**
     * Implementor can put in extra properties which can't be provided using AdeptJ JPA EntityManagerFactory configuration.
     * <p>
     * For example an object of DescriptorCustomizer implementation could be provided or one or more configurations
     * provided by AdeptJ JPA EntityManagerFactory configuration could be overridden.
     * <p>
     * Note: Be cautious while overriding configurations, this may result in unpredictable behaviour at runtime.
     *
     * @return the extra persistence info properties.
     */
    default Map<String, Object> getPersistenceUnitProperties() {
        return Collections.emptyMap();
    }
}
