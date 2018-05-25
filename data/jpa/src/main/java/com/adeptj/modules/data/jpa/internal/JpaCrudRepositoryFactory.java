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

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.persistence.EntityManagerFactory;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.data.jpa.internal.JpaCrudRepositoryFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Provides configuration for creating {@link EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(
        immediate = true,
        name = COMPONENT_NAME,
        property = SERVICE_PID + EQ + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class JpaCrudRepositoryFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.data.jpa.JpaCrudRepositoryFactory.factory";

    private Pair<EntityManagerFactory, ServiceRegistration<JpaCrudRepository>> pair;

    @Reference
    private JpaCrudRepositoryManager crudRepositoryManager;

    // ------------------------------------------------ INTERNAL ------------------------------------------------

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        this.pair = this.crudRepositoryManager.create(config);
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        this.crudRepositoryManager.dispose(config.unitName(), this.pair);
    }
}
