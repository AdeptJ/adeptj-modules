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

package com.adeptj.modules.data.jpa.internal;

import com.adeptj.modules.data.jpa.api.JpaRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.Hashtable;
import java.util.Optional;

import static org.osgi.framework.Constants.SCOPE_BUNDLE;
import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.framework.Constants.SERVICE_SCOPE;
import static org.osgi.framework.Constants.SERVICE_VENDOR;
import static org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME;

/**
 * This class is a utility which registers the {@link JpaRepository} with OSGi service registry whenever
 * there is a new EntityManagerFactory configuration saved by {@link JpaRepositoryFactory}
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
final class JpaRepositories {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaRepositories.class);

    static ServiceRegistration<JpaRepository> register(String unitName, EntityManagerFactory emf, BundleContext ctx) {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put(SERVICE_VENDOR, "AdeptJ");
        properties.put(SERVICE_PID, EclipseLinkRepository.class.getName());
        properties.put(SERVICE_DESCRIPTION, "AdeptJ JpaCrudRepository(EclipseLink)");
        properties.put(JPA_UNIT_NAME, unitName);
        properties.put(SERVICE_SCOPE, SCOPE_BUNDLE);
        LOGGER.info("Registering JpaCrudRepository For PersistenceUnit: [{}]", unitName);
        return ctx.registerService(JpaRepository.class, new EclipseLinkRepository(emf), properties);
    }

    static void unregister(String unitName, ServiceRegistration<JpaRepository> crudRepository) {
        Optional.ofNullable(crudRepository)
                .ifPresent(cr -> {
                    try {
                        cr.unregister();
                        LOGGER.info("JpaCrudRepository for PersistenceUnit: [{}] disposed!!", unitName);
                    } catch (Exception ex) { // NOSONAR
                        LOGGER.error("Exception while disposing JpaCrudRepository service!!", ex);
                    }
                });
    }
}
