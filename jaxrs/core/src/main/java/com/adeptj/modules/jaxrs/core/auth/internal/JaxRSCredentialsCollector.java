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


import com.adeptj.modules.jaxrs.core.auth.SimpleCredentials;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects the credentials config created vis OSGi web console.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = JaxRSCredentialsCollector.class)
public class JaxRSCredentialsCollector {

    private final List<SimpleCredentials> credentials = new ArrayList<>();

    void addCredentials(SimpleCredentials simpleCredentials) {
        if (this.matchCredentials(simpleCredentials)) {
            throw new IllegalStateException(String.format("Username: [%s] already present!!", simpleCredentials.getUsername()));
        }
        this.credentials.add(simpleCredentials);
    }

    void removeCredentials(SimpleCredentials simpleCredentials) {
        this.credentials.remove(simpleCredentials);
    }

    boolean matchCredentials(SimpleCredentials simpleCredentials) {
        return this.credentials.contains(simpleCredentials);
    }
}
