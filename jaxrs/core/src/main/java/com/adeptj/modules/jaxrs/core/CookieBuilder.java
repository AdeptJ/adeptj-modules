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

package com.adeptj.modules.jaxrs.core;

import javax.ws.rs.core.NewCookie;
import java.util.Date;
import java.util.Objects;

import static javax.ws.rs.core.Cookie.DEFAULT_VERSION;

/**
 * Builder for JAX-RS cookies.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CookieBuilder {

    private NewCookie cookie;

    private CookieBuilder(NewCookie cookie) {
        this.cookie = cookie;
    }

    public NewCookie getCookie() {
        return Objects.requireNonNull(this.cookie, "NewCookie instance is uninitialized!!");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {
        }

        private String name;

        private String value;

        private String path;

        private String domain;

        private String comment;

        private int maxAge;

        private Date expiry;

        private boolean secure;

        private boolean httpOnly;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder maxAge(int maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder expiry(Date expiry) {
            this.expiry = expiry;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public CookieBuilder build() {
            return new CookieBuilder(new NewCookie(this.name, this.value, this.path, this.domain, DEFAULT_VERSION,
                    this.comment,
                    this.maxAge,
                    this.expiry,
                    this.secure,
                    this.httpOnly));
        }
    }
}
