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
package com.adeptj.modules.jaxrs.base;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JaxRSAuthRepository.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Component(immediate = true, service = JaxRSAuthRepository.class)
public class JaxRSAuthRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRSAuthRepository.class);

    @Reference
    private JaxRSAuthConfigFactory authConfigFactory;

    JaxRSAuthConfig getAuthConfig(String subject) {
        LOGGER.info("Getting JaxRSAuthConfig for Subject: [{}]", subject);
        return this.authConfigFactory.getJaxRSAuthConfig(subject);
    }

}
