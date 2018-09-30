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

package com.adeptj.modules.jaxrs.core.auth.internal;

import com.adeptj.modules.jaxrs.core.auth.JaxRSCredentialsConfig;
import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.commons.utils.Constants.EQ;
import static com.adeptj.modules.jaxrs.core.auth.internal.JaxRSCredentialsFactory.COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Manages {@link JaxRSCredentialsConfig} created vis OSGi web console.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@Designate(ocd = JaxRSCredentialsConfig.class, factory = true)
@Component(
        service = JaxRSCredentialsFactory.class,
        name = COMPONENT_NAME,
        property = SERVICE_PID + EQ + COMPONENT_NAME,
        configurationPolicy = REQUIRE
)
public class JaxRSCredentialsFactory {

    static final String COMPONENT_NAME = "com.adeptj.modules.jaxrs.core.JaxRSCredentials.factory";

    private String username;

    private char[] password;

    SimpleCredentials getSimpleCredentials() {
        return SimpleCredentials.of(this.username, this.password);
    }

    @Activate
    protected void start(JaxRSCredentialsConfig config) {
        String username = config.username();
        String password = config.password();
        Validate.isTrue(StringUtils.isNotEmpty(username), "Username can't be blank!!");
        Validate.isTrue(StringUtils.isNotEmpty(password), "Password can't be blank!!");
        this.username = username;
        this.password = password.toCharArray();
    }

    @Deactivate
    protected void stop() {
        this.username = null;
        this.password = null;
    }
}
