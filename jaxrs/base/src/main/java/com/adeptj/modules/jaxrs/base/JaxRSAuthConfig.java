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

import java.util.List;

/**
 * JaxRSAuthConfig for holding REST API caller's Auth details.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class JaxRSAuthConfig {

    private String subject;

    private char[] password;

    private String signingKey;

    private List<String> origins;

    private List<String> userAgents;

    private JaxRSAuthConfig(String subject, char[] password, String signingKey) {
        this.subject = subject;
        this.password = password;
        this.signingKey = signingKey;
    }

    public String getSubject() {
        return subject;
    }

    public char[] getPassword() {
        return password;
    }

    String getSigningKey() {
        return signingKey;
    }

    public List<String> getOrigins() {
        return origins;
    }

    public List<String> getUserAgents() {
        return userAgents;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    /**
     * Builder for JaxRSAuthConfig.
     *
     * @author Rakesh.Kumar, AdeptJ.
     */
    public static class Builder {

        private String subject;

        private char[] password;

        private String signingKey;

        private List<String> origins;

        private List<String> userAgents;

        Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        Builder password(String password) {
            this.password = password.toCharArray();
            return this;
        }

        Builder signingKey(String signingKey) {
            this.signingKey = signingKey;
            return this;
        }

        Builder origins(List<String> origins) {
            this.origins = origins;
            return this;
        }

        Builder userAgents(List<String> userAgents) {
            this.userAgents = userAgents;
            return this;
        }

        JaxRSAuthConfig build() {
            JaxRSAuthConfig config = new JaxRSAuthConfig(this.subject, this.password, this.signingKey);
            config.origins = this.origins;
            config.userAgents = this.userAgents;
            return config;
        }
    }
}
