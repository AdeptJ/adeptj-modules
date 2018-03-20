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

import java.util.Objects;

/**
 * This exception must be thrown by resource methods if it is required to be implicitly handled by
 * RESTEasy via an {@link javax.ws.rs.ext.ExceptionMapper}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JaxRSException extends RuntimeException {

    private static final long serialVersionUID = 2139857593852278359L;

    private String mediaType;

    private int status;

    private Object entity;

    private boolean logException;

    private JaxRSException(String message, Throwable cause) {
        super(message, cause);
    }

    private JaxRSException(Throwable cause) {
        super(cause);
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getStatus() {
        return status;
    }

    public Object getEntity() {
        return entity;
    }

    public boolean isLogException() {
        return logException;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link JaxRSException}
     */
    public static class Builder {

        private String message;

        private Throwable cause;

        private String mediaType;

        private int status;

        private Object entity;

        private boolean logException = true;

        private Builder() {
        }

        public Builder message(String message) {
            this.message = Objects.requireNonNull(message, "message must not be null!!");
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = Objects.requireNonNull(cause, "Exception must not be null!!");
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        public Builder logException(boolean logException) {
            this.logException = logException;
            return this;
        }

        public JaxRSException build() {
            JaxRSException exception = new JaxRSException(this.message, this.cause);
            exception.status = this.status;
            exception.mediaType = this.mediaType;
            exception.entity = this.entity;
            exception.logException = this.logException;
            return exception;
        }
    }
}
