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
package com.adeptj.modules.jaxrs.core.internal;

import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfo;
import com.adeptj.modules.jaxrs.core.JaxRSAuthenticationInfoFactory;
import com.adeptj.modules.jaxrs.core.api.JaxRSAuthenticationRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of JaxRSAuthenticationRepository.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true)
public class JaxRSDefaultAuthenticationRepository implements JaxRSAuthenticationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSDefaultAuthenticationRepository.class);

    private static final String DEFAULT_NAME = "Default JaxRSAuthenticationRepository";

    @Reference
    private JaxRSAuthenticationInfoFactory authenticationInfoFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return DEFAULT_NAME;
    }

    @Override
    public JaxRSAuthenticationInfo getAuthenticationInfo(String subject, String password) {
        LOGGER.info("Getting JaxRSAuthenticationInfo for Subject: [{}]", subject);
        return this.authenticationInfoFactory.getAuthenticationInfo(subject, password);
    }

}
