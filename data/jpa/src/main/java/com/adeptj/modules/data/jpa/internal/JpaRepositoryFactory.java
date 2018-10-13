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

import com.adeptj.modules.commons.utils.annotation.ServicePid;
import com.adeptj.modules.data.jpa.api.JpaRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.persistence.EntityManagerFactory;

import static com.adeptj.modules.data.jpa.internal.JpaRepositoryFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Provides configuration for creating {@link EntityManagerFactory}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ServicePid(PID)
@Designate(ocd = EntityManagerFactoryConfig.class, factory = true)
@Component(immediate = true, name = PID, configurationPolicy = REQUIRE)
public class JpaRepositoryFactory {

    static final String PID = "com.adeptj.modules.data.jpa.JpaRepository.factory";

    private Pair<EntityManagerFactory, ServiceRegistration<JpaRepository>> pair;

    @Reference
    private JpaRepositoryManager repositoryManager;

    // ------------------------------------------------ INTERNAL ------------------------------------------------

    @Activate
    protected void start(EntityManagerFactoryConfig config) {
        this.pair = this.repositoryManager.create(config);
    }

    @Deactivate
    protected void stop(EntityManagerFactoryConfig config) {
        this.repositoryManager.dispose(config.unitName(), this.pair);
    }
}
