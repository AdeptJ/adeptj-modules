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

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JaxRSAuthConfigProvider.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public enum JaxRSAuthConfigProvider {

    INSTANCE;

    private static final String ALGO_SHA256 = "SHA-256";

    private static final String UTF8 = "UTF-8";

    private Map<String, JaxRSAuthConfig> configs = new HashMap<>();

    public void addJaxRSAuthConfig(JaxRSAuthConfig config) throws UnsupportedEncodingException {
        // Update the signing key to Base64 encoded version.
        config.setSigningKey(new String(Base64.getEncoder().encode(config.getSigningKey().getBytes(UTF8))));
        this.configs.put(config.getSubject(), config);
    }

    public JaxRSAuthConfig deleteJaxRSAuthConfig(String subject) {
        return this.configs.remove(subject);
    }

    public JaxRSAuthConfig getJaxRSAuthConfig(String subject) {
        return this.configs.get(subject);
    }
}
